//package ch.ethz.asl.ca.service;
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.security.Principal;
//
//
///**
// * Wrapper for shell execution functionality.
// */
//@Component
//public class ShellExecutionWrapper {
//    private final String SIGN_CERTIFICATE = "sudo openssl ca -in key.csr -config /etc/ssl/openssl.cnf";
//    private final String REVOKE_CERTIFICATE = "sudo openssl ca -revoke cert.crt -config /etc/ssl/openssl.cnf";
//    private final String CREATE_CRL = "sudo openssl ca -gencrl -out /etc/ssl/CA/crl/crl.pem"
//
//
//    public void issueNewCertificate(final String username) {
//
//        Runtime.getRuntime().exec(SIGN_CERTIFICATE);
//    }
//
//    public void getCertificate(final String serialNr) {
//
//        try {
//            Runtime.getRuntime().exec(SIGN_CERTIFICATE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void revokeCertificate(final String serialNr, final Principal principal) {
//
//    }
//}
