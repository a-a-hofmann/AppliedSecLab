package ch.ethz.asl.gateway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserCertificate implements Serializable, Comparable<UserCertificate> {

    private String serialNr;

    private String issuedTo;

    private Timestamp issuedOn;

    private Timestamp revokedOn;

    private boolean isRevoked;

    @JsonCreator
    public UserCertificate(@JsonProperty("serialNr") String serialNr, @JsonProperty("issuedTo") String issuedTo,
                           @JsonProperty("issuedOn") Timestamp issuedOn, @JsonProperty("revokedOn") Timestamp revokedOn,
                           @JsonProperty("isRevoked") boolean isRevoked) {
        this.serialNr = serialNr;
        this.issuedTo = issuedTo;
        this.issuedOn = issuedOn;
        this.revokedOn = revokedOn;
        this.isRevoked = isRevoked;
    }

    public String getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(String serialNr) {
        this.serialNr = serialNr;
    }

    public String getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
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

    @Override
    public int compareTo(UserCertificate o) {
        return issuedOn.compareTo(o.issuedOn);
    }
}
