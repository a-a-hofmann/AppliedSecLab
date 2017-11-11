package ch.ethz.asl.gateway;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserCertificate implements Serializable {

    private long serialNr;

    private Timestamp issuedOn;

    private Timestamp revokedOn;

    private boolean isRevoked;

    @JsonCreator
    public UserCertificate(@JsonProperty("serialNr") long serialNr, @JsonProperty("issuedOn") Timestamp issuedOn,
                           @JsonProperty("revokedOn") Timestamp revokedOn, @JsonProperty("isRevoked") boolean isRevoked) {
        this.serialNr = serialNr;
        this.issuedOn = issuedOn;
        this.revokedOn = revokedOn;
        this.isRevoked = isRevoked;
    }

    public long getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(long serialNr) {
        this.serialNr = serialNr;
    }

    public Timestamp getIssuedOn() {
        return issuedOn;
    }

    public void setIssuedOn(Timestamp issuedOn) {
        this.issuedOn = issuedOn;
    }

    public Timestamp getRevokedOn() {
        return revokedOn;
    }

    public void setRevokedOn(Timestamp revokedOn) {
        this.revokedOn = revokedOn;
    }

    public boolean isRevoked() {
        return isRevoked;
    }

    public void setRevoked(boolean revoked) {
        isRevoked = revoked;
    }
}
