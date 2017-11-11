package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.service.UserCertificateService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;


@Component
public class OpenSSL implements CertificateManager {


    //TODO: Only for developing. Must be changed to -> /etc/ssl/
    private static final String dir = "etc/ssl/";
    private static final String SERIALNR_OLD_PATH = dir + "CA/serial.old";
    private static final String CRLNR_OLD_PATH = dir + "CA/crlnumber.old";

    private static final String SERIALNR_PATH = dir + "CA/serial";

    private static final String CERTIFICATE_PATH = dir + "CA/newcerts/%s/%s.pem";
    //TODO: Only for developing
    private static final String GENERATE_KEY_PATH = dir + "CA/newkeys/%s/";
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
    private final String GENERATE_SIGNING_REQUEST = "openssl req -new -key %s.key -out %s.csr -config"+ dir + "openssl.cnf " + SUBJ;

    //Example: openssl ca -name CA_db -batch -in etc/ssl/CA/newkeys/test.csr -config etc/ssl/openssl.cnf
    private final String SIGN_CERTIFICATE = "openssl ca -name CA_%s -batch -in %s.csr -config " + dir + "openssl.cnf -passin pass:admin";
    private final String REVOKE_CERTIFICATE = "sudo openssl ca -revoke %s -config " + dir + "openssl.cnf";
    private final String CREATE_CRL = "sudo openssl ca -gencrl -out " + dir + "CA/crl/crl.pem";

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
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssSS'.key'").format(new Date());
        String file = String.format(GENERATE_KEY_PATH + fileName, username);
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_KEY, file));
            return file;
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_RSA_KEY + e.getMessage());
        }
    }

    private void generateSigningRequest(final String keyPath, User user) throws CertificateManagerException {
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_SIGNING_REQUEST, keyPath, keyPath,
                                                    user.getFirstname(),
                                                    user.getLastname(),
                                                    user.getEmail()));

        } catch (IOException e) {
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
                Process p = Runtime.getRuntime().exec(String.format(SIGN_CERTIFICATE, user.getUsername(), currentPath));
                p.waitFor();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(SERIALNR_OLD_PATH));
                    serialNr = br.readLine();
                    br.close();
                } catch (IOException e) {
                    throw new CertificateManagerException(UNABLE_TO_READ_LAST_ISSUED_CERTIFICATE + e.getMessage());
                }
            }

        } catch (IOException|InterruptedException e) {
            throw new CertificateManagerException(UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION + e.getMessage());
        }

        return serialNr;
    }

    @Override
    public boolean getCertificate(final String serialNr, final User user, ServletOutputStream outputStream) throws CertificateManagerException {


        try {
            FileInputStream fis = new FileInputStream(String.format(CERTIFICATE_PATH, user.getUsername(), serialNr));
            IOUtils.copy(fis, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new CertificateManagerException();
        }
        return true;
    }

    @Override
    public boolean revokeCertificate(final String serialNr, final User user) throws CertificateManagerException {

        String certificatePath = String.format(CERTIFICATE_PATH, user.getUsername(), serialNr);
        try {
            Runtime.getRuntime().exec(String.format(REVOKE_CERTIFICATE, certificatePath));
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_REVOKE_CERTIFICATE + e.getMessage());
        }

        try {
            synchronized (CertificateManager.class) {
                Runtime.getRuntime().exec(CREATE_CRL);
            }
        } catch (IOException e) {
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

}
