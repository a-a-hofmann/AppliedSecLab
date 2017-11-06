package ch.ethz.asl.ca.service.command;

import ch.ethz.asl.ca.model.UserSafeProjection;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
import java.security.cert.X509Certificate;


@Component
public class OpenSSL implements CertificateManager {

    //Only for developing. Must be changed to -> /etc/ssl/
    private final String dir = "etc/ssl/";

    private final String UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION = "Unable to sign the certificate by using openssl. Error: ";
    private final String UNABLE_TO_GENERATE_RSA_KEY = "Unable to generate a RSA key by using openssl. Error: ";
    private final String UNABLE_TO_GENERATE_SINGNING_REQUEST = "Unable to generate a signing request by using openssl. Error: ";
    private final String UNABLE_TO_REVOKE_CERTIFICATE = "Unable to revoke the given certificate by using openssl. Error: ";



    private final String GENERATE_KEY = "openssl genrsa -out %s.key 1024";
    //Find a better way, where to store the generated keys and add -subj.
    private final String GENERATE_SIGNING_REQUEST = "openssl req -new -key %s.key -out %s.csr";

    private final String GET_CERTIFICATE = "";
    private final String SIGN_CERTIFICATE = "sudo openssl ca -in %s.csr -config " + dir + "openssl.cnf";
    private final String REVOKE_CERTIFICATE = "sudo openssl ca -revoke %s -config " + dir + "openssl.cnf";
    private final String CREATE_CRL = "sudo openssl ca -gencrl -out " + dir + "CA/crl/crl.pem";

    private void generateKey(final String username) throws CertificateManagerException {
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_KEY, username));
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_RSA_KEY + e.getMessage());
        }
    }

    private void generateSigningRequest(final String username) throws CertificateManagerException{
        try {
            Runtime.getRuntime().exec(String.format(GENERATE_SIGNING_REQUEST, username));
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_GENERATE_SINGNING_REQUEST + e.getMessage());
        }
    }

    // return object???
    public X509Certificate issueNewCertificate(UserSafeProjection user) throws CertificateManagerException{
        generateKey(user.getUsername());
        generateSigningRequest(user.getUsername());

        try {
            Runtime.getRuntime().exec(String.format(SIGN_CERTIFICATE, user.getUsername()));
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_SIGN_CERTIFICATE_EXCEPTION + e.getMessage());
        }
        return null;
    }

    public X509Certificate getCertificate(final String serialNr) {

        try {
            Runtime.getRuntime().exec(SIGN_CERTIFICATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean revokeCertificate(final String serialNr, final Principal principal) throws CertificateManagerException {
        boolean success = true;
        String certificate = null;

        // locate the certificate based on the serialNr.

        try {
            Runtime.getRuntime().exec(String.format(REVOKE_CERTIFICATE, certificate));
        } catch (IOException e) {
            throw new CertificateManagerException(UNABLE_TO_REVOKE_CERTIFICATE + e.getMessage());
        }
        return false;
    }

}
