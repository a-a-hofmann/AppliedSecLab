package ch.ethz.asl.auth.config;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationApi {

    private final RestTemplate restTemplate;

    private static final String AUTHENTICATION_URL = "https://localhost:8445/authenticate";

    private static final String EMAIL_QUERY_URL = "https://localhost:8445/authenticate/email";

    @Value("${client-secret}")
    private String apiKey;

    @Autowired
    public AuthenticationApi() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> queryByEmail(String email) {
        HttpEntity<String> entity = new HttpEntity<>(createHeaders(email));
        return restTemplate.exchange(EMAIL_QUERY_URL, HttpMethod.POST, entity, String.class);
    }

    public ResponseEntity<?> authenticate(String username, String password) {
        HttpEntity<?> entity = usernamePasswordToken(username, password);
        return restTemplate.exchange(AUTHENTICATION_URL, HttpMethod.POST, entity, Object.class);
    }

    private HttpHeaders createHeaders(final String email) {
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> accept = new ArrayList<>();
        accept.add(MediaType.APPLICATION_JSON);
        headers.setAccept(accept);

        headers.set("Authorization", email);
        headers.set("X-Authorization", apiKey);
        return headers;
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
