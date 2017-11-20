package ch.ethz.asl.ca.service;

public class CertificateIsRevokedException extends Exception {
    public CertificateIsRevokedException(String message) {
        super(message);
    }
}
