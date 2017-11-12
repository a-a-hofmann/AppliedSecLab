package ch.ethz.asl.gateway;


import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

public class CertificateRevocationCommand {

    private static final PasswordEncoder PASSWORD_ENCODER = new ShaPasswordEncoder();

    public String password;

    public String serialNr;

    public CertificateRevocationCommand() {
    }

    public CertificateRevocationCommand(String password, String serialNr) {
        this.password = PASSWORD_ENCODER.encodePassword(password, null);
        this.serialNr = serialNr;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = PASSWORD_ENCODER.encodePassword(password, null);
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
                "password='" + password + '\'' +
                ", serialNr='" + serialNr + '\'' +
                '}';
    }
}
