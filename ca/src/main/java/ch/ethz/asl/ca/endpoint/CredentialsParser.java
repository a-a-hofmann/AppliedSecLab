package ch.ethz.asl.ca.endpoint;

import com.google.common.base.Splitter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
public class CredentialsParser {

    public Credentials parseHeaderForCredentials(String authorizationHeader) {
        List<String> strings = Splitter.on(" ").trimResults().splitToList(authorizationHeader);
        Assert.isTrue(strings.size() == 2, String.format("Didn't find Basic Auth header. Found %s", authorizationHeader));
        String basicAuth = strings.get(1);
        String basic = new String(Base64.decodeBase64(basicAuth));
        List<String> usernamePasswordToken = Splitter.on(":").trimResults().splitToList(basic);
        Assert.isTrue(usernamePasswordToken.size() == 2, String.format("Couldn't parse credentials. Found %s", basic));
        return new Credentials(usernamePasswordToken.get(0), usernamePasswordToken.get(1));
    }

    public static class Credentials {

        private static final PasswordEncoder PASSWORD_ENCODER = new ShaPasswordEncoder();//.encodePassword(user.getPassword(), null);

        private final String username;

        private final String password;

        public Credentials(String username, String password) {
            this.username = username;
            this.password = PASSWORD_ENCODER.encodePassword(password, null);
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
