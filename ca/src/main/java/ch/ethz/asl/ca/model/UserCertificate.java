package ch.ethz.asl.ca.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;

@Entity(name = "certs")
public class UserCertificate implements Serializable {

    @Id
    private long serialNr;

    private Date issuedOn;

    private Date revokedOn;

    private boolean isRevoked;

    private String path;

    @ManyToOne
    private User issuedTo;

    public UserCertificate() {
    }

    public long getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(long serialNr) {
        this.serialNr = serialNr;
    }

    public Date getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Date issuedOn) {
        this.issuedOn = issuedOn;
    }

    public Date getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(Date revokedOn) {
        this.revokedOn = revokedOn;
    }

    public void revoke() {
        this.revokedOn = new Date();
        this.isRevoked = true;
    }

    public User getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(User issuedTo) {
        this.issuedTo = issuedTo;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserCertificate that = (UserCertificate) o;

        if (serialNr != that.serialNr) return false;
        if (isRevoked != that.isRevoked) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return issuedTo != null ? issuedTo.equals(that.issuedTo) : that.issuedTo == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (serialNr ^ (serialNr >>> 32));
        result = 31 * result + (isRevoked ? 1 : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (issuedTo != null ? issuedTo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserCertificate{" +
                "serialNr=" + serialNr +
                ", issuedOn=" + issuedOn +
                ", revokedOn=" + revokedOn +
                ", isRevoked=" + isRevoked +
                ", path='" + path + '\'' +
                ", issuedTo=" + issuedTo +
                '}';
    }

    public static UserCertificate issuedNow() {
        UserCertificate certificate = new UserCertificate();
        certificate.issuedOn = new Date();
        return certificate;
    }
}
