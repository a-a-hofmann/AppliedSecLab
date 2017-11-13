package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.User;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class OpenSSL implements CertificateManager {

    //TODO: Only for developing. Must be changed to -> /etc/ssl/
    private static final String dir = "ca/etc/ssl/";
    private static final String ABSOLUTE_DIR = Paths.get("ca/etc/ssl/").toAbsolutePath().toString() + "/";
    private static final String SERIALNR_OLD_PATH = ABSOLUTE_DIR + "CA/serial.old";
    private static final String CRLNR_OLD_PATH = ABSOLUTE_DIR + "CA/crlnumber.old";

    private static final String SERIALNR_PATH = ABSOLUTE_DIR + "CA/serial";

    private static final String CERTIFICATE_PATH = ABSOLUTE_DIR + "CA/newcerts/%s/%s.pem";
    private static final String CERTIFICATE_P12_PATH = ABSOLUTE_DIR + "CA/newcerts/%s/%s.p12";
    //TODO: Only for developing
    private static final String GENERATE_KEY_PATH = ABSOLUTE_DIR + "CA/newkeys/%s/";
    //TODO:NAME??
    private static final String SUBJ = "-subj /C=CH/ST=Zurich/L=Zurich/O=ETH/OU=AppliedSecLab/CN=%s_%s/emailAddress=%s";

    private static final String UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION = "Unable to sign the certificate by using openssl. Error: ";
    private static final String UNABLE_TO_GENERATE_RSA_KEY = "Unable to generate a RSA key by using openssl. Error: ";
    private static final String UNABLE_TO_GENERATE_SIGNING_REQUEST = "Unable to generate a signing request by using openssl. Error: ";
    private static final String UNABLE_TO_REVOKE_CERTIFICATE = "Unable to revoke the given certificate by using openssl. Error: ";
    private static final String UNABLE_TO_CREATE_CRL = "Unable to create the revokation list. Error: ";
    private static final String UNABLE_TO_FIND_CERTIFICATE = "The certificate with the given serial number does not exist. Error: ";
    private static final String UNABLE_TO_READ_LAST_ISSUED_CERTIFICATE = "Unable to read the last issued certificate from the filesstem. Error: ";
    private static final String UNABLE_TO_GET_NR_OF_ISSUED_CERTIFICATES = "Unable to get the number of issued certificates from the filesystem. Error: ";
    private static final String UNABLE_TO_GET_NR_OF_REVOKED_CERTIFICATES = "Unable to get the number of revoked certificates from the filesystem. Error: ";
    private static final String UNABLE_TO_GET_CURRENT_SERIAL_NUMBER = "Unable to get the current serial number from the filesystem. Error: ";


    //TODO: %s mitigate injection for all
    private final String GENERATE_KEY = "openssl genrsa -out %s.key 1024";
    //Example:openssl req -new -key etc/ssl/CA/newkeys/db/test.key -out etc/ssl/CA/newkeys/db/test.csr -config etc/ssl/openssl.cnf -subj "/C=CH/ST=Zurich/L=Zurich/O=ETH/OU=AppliedSecLab/CN=Test test/emailAddress=test@imovie.ch"
    private final String GENERATE_SIGNING_REQUEST = "openssl req -new -key %s.key -out %s.csr -config " + ABSOLUTE_DIR + "openssl.cnf " + SUBJ;

    //Example: openssl ca -name CA_db -batch -in etc/ssl/CA/newkeys/test.csr -config etc/ssl/openssl.cnf
    private final String SIGN_CERTIFICATE = "openssl ca -name CA_%s -batch -in %s.csr -config " + ABSOLUTE_DIR + "openssl.cnf -passin pass:admin";
    private final String REVOKE_CERTIFICATE = "openssl ca -revoke %s -config " + ABSOLUTE_DIR + "openssl.cnf";
    private final String CREATE_CRL = "openssl ca -gencrl -out " + ABSOLUTE_DIR + "CA/crl/crl.pem";
    private final String CREATE_P12 = "openssl pkcs12 -export -out %s -inkey %s -in %s -passout pass:";

    //private final UserCertificateService userCertificateService;


    //TODO: probably the best way to mitigate command injection
    private String execute(String command) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(command);

        OutputStream stdOut = p.getOutputStream();
        BufferedReader commandOutput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        PemReader reader = new PemReader(commandOutput);

        StringBuilder toReturn = new StringBuilder();
        String s;
        while ((s = commandOutput.readLine()) != null) {
            toReturn.append(s);
        }
        p.waitFor();
        return toReturn.toString();
    }


    private String generateKey(final String username) throws CertificateManagerException {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
        String file = String.format(GENERATE_KEY_PATH + fileName, username);
        try {
            final String generateKeyCommand = String.format(GENERATE_KEY, file);
            new ProcessUtils().runBlockingProcess(generateKeyCommand);
            return file;
        } catch (IOException | InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_RSA_KEY + e.getMessage());
        }
    }

    private void generateSigningRequest(final String keyPath, User user) throws CertificateManagerException {
        try {
            final String absoluteKeyPath = Paths.get(keyPath).toAbsolutePath().toString();
            final String signingRequestCommand = String.format(GENERATE_SIGNING_REQUEST, absoluteKeyPath, absoluteKeyPath,
                    user.getFirstname().replace(" ", "_"),
                    user.getLastname().replace(" ", "_"),
                    user.getEmail());

            new ProcessUtils().runBlockingProcess(signingRequestCommand);
        } catch (IOException | InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_SIGNING_REQUEST + e.getMessage());
        }
    }


    @Override
    public String issueNewCertificate(User user) throws CertificateManagerException {

        String currentPath = generateKey(user.getUsername());
        generateSigningRequest(currentPath, user);
        String serialNr = null;

        try {
            synchronized (CertificateManager.class) {
                final String command = String.format(SIGN_CERTIFICATE, user.getUsername(), Paths.get(currentPath).toAbsolutePath().toString());

                ProcessUtils processUtils = new ProcessUtils();
                processUtils.runBlockingProcess(command);

                try {
                    BufferedReader br = new BufferedReader(new FileReader(SERIALNR_OLD_PATH));
                    serialNr = br.readLine();
                    br.close();
                    // TODO: after signing we should remove the csr file.
                } catch (IOException e) {
                    throw new CertificateManagerException(UNABLE_TO_READ_LAST_ISSUED_CERTIFICATE + e.getMessage());
                }

                createPKCS12File(serialNr, currentPath, user);
            }

        } catch (IOException | InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION + e.getMessage());
        }

        return serialNr;
    }

    private void createPKCS12File(final String serialNr, final String currentPath, final User user) throws IOException, InterruptedException {
        final String p12Path = Paths.get(String.format(CERTIFICATE_P12_PATH, user.getUsername(), serialNr)).toAbsolutePath().toString();
        final String pemPath = String.format(CERTIFICATE_PATH, user.getUsername(), serialNr);
        final String privateKeyPath = Paths.get(currentPath).toAbsolutePath().toString() + ".key";
        final String p12FormatCommand = String.format(CREATE_P12, p12Path, privateKeyPath, pemPath);
        new ProcessUtils().runBlockingProcess(p12FormatCommand);
    }

    @Override
    public byte[] getCertificate(final String serialNr, final User user) throws CertificateManagerException {
        // TODO: cert should be exported as .p12
        try {
            return FileUtils.readFileToByteArray(new File(String.format(CERTIFICATE_P12_PATH, user.getUsername(), serialNr)));
        } catch (IOException e) {
            throw new CertificateManagerException(e);
        }
    }

    @Override
    public boolean revokeCertificate(final String serialNr, final User user) throws CertificateManagerException {

        String certificatePath = String.format(CERTIFICATE_PATH, user.getUsername(), serialNr);
        ProcessUtils processUtils = new ProcessUtils();
        try {
            processUtils.runBlockingProcess(String.format(REVOKE_CERTIFICATE, certificatePath));
        } catch (IOException | InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_REVOKE_CERTIFICATE + e.getMessage());
        }

        try {
            synchronized (CertificateManager.class) {
                processUtils.runBlockingProcess(CREATE_CRL);
            }
        } catch (IOException | InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_CREATE_CRL + e.getMessage());
        }
        return true;
    }

    @Override
    public Long getNumberOfIssuedCertificates() throws CertificateManagerException {

        String serialNr;
        try {
            synchronized (CertificateManager.class) {
                BufferedReader br = new BufferedReader(new FileReader(SERIALNR_OLD_PATH));
                serialNr = br.readLine();
                br.close();
            }
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GET_NR_OF_ISSUED_CERTIFICATES + e.getMessage());
        }
        return Long.parseLong(serialNr, 16);
    }

    @Override
    public Long getNumberOfRevokedCertificates() throws CertificateManagerException {
        String crlNr;
        try {
            synchronized (CertificateManager.class) {
                BufferedReader br = new BufferedReader(new FileReader(CRLNR_OLD_PATH));
                crlNr = br.readLine();
                br.close();
            }
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GET_NR_OF_REVOKED_CERTIFICATES + e.getMessage());
        }
        return Long.parseLong(crlNr, 16);
    }

    @Override
    public String getCurrentSerialNumber() throws CertificateManagerException {

        String serialNr;
        try {
            synchronized (CertificateManager.class) {
                BufferedReader br = new BufferedReader(new FileReader(SERIALNR_PATH));
                serialNr = br.readLine();
                br.close();
            }
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GET_CURRENT_SERIAL_NUMBER + e.getMessage());
        }
        return serialNr;
    }

    @Override
    public String getPath(String serialNr, User user) {
        String filePath = String.format(CERTIFICATE_PATH, user.getUsername(), serialNr);
        File f = new File(filePath);
        if (f.exists()) {
            return filePath;
        }
        throw new IllegalArgumentException(String.format("No cert found for user [%s] and cert [%s]", user.getUsername(), serialNr));
    }
}
