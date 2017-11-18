package ch.ethz.asl.ca.model;

import java.io.Serializable;

public class AdminReport implements Serializable {

    private final Long numberOfIssuedCertificates;

    private final Long numberOfRevokedCertificates;

    private final String currentSerialNumber;

    public AdminReport(Long numberOfIssuedCertificates, Long numberOfRevokedCertificates, String currentSerialNumber) {
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
