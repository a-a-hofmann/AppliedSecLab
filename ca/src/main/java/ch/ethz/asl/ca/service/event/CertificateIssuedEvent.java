package ch.ethz.asl.ca.service.event;

/**
 * Represents the issuing of a cert.
 */
public class CertificateIssuedEvent extends Event {

    private final String serialNr;

    public CertificateIssuedEvent(String user, final String serialNr) {
        super(user);
        this.serialNr = serialNr;
    }

    public String getSerialNr() {
        return serialNr;
    }
}
