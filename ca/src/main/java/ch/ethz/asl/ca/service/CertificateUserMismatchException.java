package ch.ethz.asl.ca.service;

public class CertificateUserMismatchException extends Exception {
    public CertificateUserMismatchException(String message) {
        super(message);
    }
}
