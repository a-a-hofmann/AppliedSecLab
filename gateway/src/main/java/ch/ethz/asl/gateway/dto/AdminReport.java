package ch.ethz.asl.gateway.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@SuppressWarnings("unused")
public class AdminReport implements Serializable {

    private final Long numberOfIssuedCertificates;

    private final Long numberOfRevokedCertificates;

    private final String currentSerialNumber;

    @JsonCreator
    public AdminReport(
            @JsonProperty("numberOfIssuedCertificates") Long numberOfIssuedCertificates,
            @JsonProperty("numberOfRevokedCertificates") Long numberOfRevokedCertificates,
            @JsonProperty("currentSerialNumber") String currentSerialNumber) {
        this.numberOfIssuedCertificates = numberOfIssuedCertificates;
        this.numberOfRevokedCertificates = numberOfRevokedCertificates;
        this.currentSerialNumber = currentSerialNumber;
    }

    public Long getNumberOfIssuedCertificates() {
        return numberOfIssuedCertificates;
    }

    public Long getNumberOfRevokedCertificates() {
        return numberOfRevokedCertificates;
    }

    public String getCurrentSerialNumber() {
        return currentSerialNumber;
    }
}
