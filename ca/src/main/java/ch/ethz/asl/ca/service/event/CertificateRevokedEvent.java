package ch.ethz.asl.ca.service.event;

/**
 * Represents the revocation of a cert.
 */
public class CertificateRevokedEvent extends Event {

    private final String serialNr;

    public CertificateRevokedEvent(String user, String serialNr) {
        super(user);
        this.serialNr = serialNr;
    }

    public String getSerialNr() {
        return serialNr;
    }
}
