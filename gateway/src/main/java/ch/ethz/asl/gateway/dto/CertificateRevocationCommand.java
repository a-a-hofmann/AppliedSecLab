package ch.ethz.asl.gateway.dto;


public class CertificateRevocationCommand {

    public String serialNr;

    public CertificateRevocationCommand() {
    }

    public CertificateRevocationCommand(String password, String serialNr) {
        this.serialNr = serialNr;
    }

    public String getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(String serialNr) {
        this.serialNr = serialNr;
    }

    @Override
    public String toString() {
        return "CertificateRevocationCommand{" +
                ", serialNr='" + serialNr + '\'' +
                '}';
    }
}
