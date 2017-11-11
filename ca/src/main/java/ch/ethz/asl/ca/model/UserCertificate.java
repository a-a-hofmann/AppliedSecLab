package ch.ethz.asl.ca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity(name = "certs")
public class UserCertificate implements Serializable {

    @Id
    private Long serialNr;

    private Timestamp issuedOn;

    private Timestamp revokedOn;

    private boolean isRevoked;

    private String path;

    @ManyToOne(optional = false)
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
        this.issuedOn = new Timestamp(issuedOn.getTime());
    }

    public Date getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(Date revokedOn) {
        this.revokedOn = new Timestamp(revokedOn.getTime());
    }

    public void revoke() {
        setRevokedOn(new Date());
        this.isRevoked = true;
    }

    @JsonIgnore
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

    @JsonIgnore
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

        if (serialNr != null ? !serialNr.equals(that.serialNr) : that.serialNr != null) return false;
        if (issuedOn != null ? !issuedOn.equals(that.issuedOn) : that.issuedOn != null) return false;
        return issuedTo != null ? issuedTo.equals(that.issuedTo) : that.issuedTo == null;
    }

    @Override
    public int hashCode() {
        int result = serialNr != null ? serialNr.hashCode() : 0;
        result = 31 * result + (issuedOn != null ? issuedOn.hashCode() : 0);
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
        certificate.setIssuedOn(new Date());
        return certificate;
    }

    public static UserCertificate issuedNowToUser(final long serialNr, final String path, User user) {
        UserCertificate certificate = new UserCertificate();
        certificate.setSerialNr(serialNr);
        certificate.setIssuedOn(new Date());
        certificate.setIssuedTo(user);
        certificate.setPath(path);
        return certificate;
    }
}
