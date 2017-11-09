package ch.ethz.asl.ca.service.event;

/**
 * Represents the requesting of a cert.
 */
public class CertificateRequestedEvent extends Event {

    private final String serialNr;

    public CertificateRequestedEvent(String user, String serialNr) {
        super(user);
        this.serialNr = serialNr;
    }

    public String getSerialNr() {
        return serialNr;
    }
}
