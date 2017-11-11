package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.UserCertificateService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class OpenSSL implements CertificateManager {

    //TODO: Only for developing. Must be changed to -> /etc/ssl/
    private final String dir = "etc/ssl/";
    //TODO: Only for developing
    private final String GENERATE_KEY_PATH = "etc/ssl/CA/newkeys/%s/";

    private final String UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION = "Unable to sign the certificate by using openssl. Error: ";
    private final String UNABLE_TO_GENERATE_RSA_KEY = "Unable to generate a RSA key by using openssl. Error: ";
    private final String UNABLE_TO_GENERATE_SIGNING_REQUEST = "Unable to generate a signing request by using openssl. Error: ";
    private final String UNABLE_TO_REVOKE_CERTIFICATE = "Unable to revoke the given certificate by using openssl. Error: ";
    private final String UNABLE_TO_CREATE_CRL = "Unable to create the revokation list. Error: ";
    private final String UNABLE_TO_FIND_CERTIFICATE = "The certificate with the given serial number does not exist. Error: ";


    //TODO: %s mitigate injection for all
    private final String GENERATE_KEY = "openssl genrsa -out %s.key 1024";
    //add subj
    private final String GENERATE_SIGNING_REQUEST = "openssl req -new -key %s.key -out %s.csr";
    private final String CERTIFICATE_PATH = "etc/ssl/CA/newcerts/%s/%s.pem";
    private final String SIGN_CERTIFICATE = "sudo openssl ca -in %s.csr -config " + dir + "openssl.cnf";
    private final String REVOKE_CERTIFICATE = "sudo openssl ca -revoke %s -config " + dir + "openssl.cnf";
    private final String CREATE_CRL = "sudo openssl ca -gencrl -out " + dir + "CA/crl/crl.pem";

    private final UserCertificateService userCertificateService;

    @Autowired
    public OpenSSL(UserCertificateService userCertificateService) {
        this.userCertificateService = userCertificateService;
    }

    private String generateKey(final String username) throws CertificateManagerException {
        String fileName = new SimpleDateFormat("yyyyMMddHHmmssSS'.key'").format(new Date());
        String file = GENERATE_KEY_PATH + fileName;
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_KEY, username));
            return file;
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_RSA_KEY + e.getMessage());
        }
    }

    private void generateSigningRequest(final String keyPath) throws CertificateManagerException {
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_SIGNING_REQUEST, keyPath, keyPath));

        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_SIGNING_REQUEST + e.getMessage());
        }
    }

    //TODO:
    public String issueNewCertificate(UserSafeProjection user) throws CertificateManagerException {
        String currentPath = generateKey(user.getUsername());
        generateSigningRequest(currentPath);

        try {
            Runtime.getRuntime().exec(String.format(SIGN_CERTIFICATE, currentPath));

            // Store cert in db
            // TODO get serialNr
            // TODO set path
            userCertificateService.issueCertificateForUser(user, 12345, "/");
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION + e.getMessage());
        }
        //how to get the serial number
        return null;
    }

    //TODO:
    public X509Certificate getCertificate(final String serialNr, final Principal principal) throws CertificateManagerException {

        try {
            FileInputStream fis = new FileInputStream(String.format(CERTIFICATE_PATH, principal.getName(), serialNr));
            BouncyCastleProvider provider = new BouncyCastleProvider();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509", provider);
            X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(fis);
            return certificate;
        } catch (FileNotFoundException | CertificateException e) {
            throw new CertificateManagerException(UNABLE_TO_FIND_CERTIFICATE + e.getMessage());
        }
    }


    public void revokeCertificate(final String serialNr, final Principal principal) throws CertificateManagerException {
        String certificatePath = String.format(CERTIFICATE_PATH, principal.getName(), serialNr);

        try {
            Runtime.getRuntime().exec(String.format(REVOKE_CERTIFICATE, certificatePath));

        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_REVOKE_CERTIFICATE + e.getMessage());
        }
        try {
            Runtime.getRuntime().exec(CREATE_CRL);
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_CREATE_CRL + e.getMessage());
        }

    }

}
