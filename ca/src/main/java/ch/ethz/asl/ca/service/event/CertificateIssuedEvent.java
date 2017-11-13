package ch.ethz.asl.ca.service.event;

/**
 * Represents the issuing of a cert.
 */
public class CertificateIssuedEvent extends Event {

    public CertificateIssuedEvent(String user) {
        super(user);
    }

}
