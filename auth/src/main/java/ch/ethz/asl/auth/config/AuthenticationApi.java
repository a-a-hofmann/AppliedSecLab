package ch.ethz.asl.auth.config;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationApi {

    private static final Logger logger = Logger.getLogger(AuthenticationApi.class);

    private final RestTemplate restTemplate;

    private static final String URI = "http://localhost:8081/authenticate";

    @Value("${client-secret}")
    private String apiKey;

    @Autowired
    public AuthenticationApi() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<?> authenticate(String username, String password) {
        HttpEntity<?> entity = usernamePasswordToken(username, password);
        try {
            return restTemplate.exchange(URI, HttpMethod.POST, entity, Object.class);
        } catch (HttpClientErrorException e) {
            logger.error("Bad credentials", e);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity<?> usernamePasswordToken(String username, String password) {
        return new HttpEntity<>(createHeaders(username, password));
    }

    private HttpHeaders createHeaders(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> accept = new ArrayList<>();
        accept.add(MediaType.APPLICATION_JSON);
        headers.setAccept(accept);

        String auth = username + ":" + password;
        String basic = new String(Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8"))));
        headers.set("Authorization", "Basic " + basic);
        headers.set("X-Authorization", apiKey);
        return headers;
    }
}
