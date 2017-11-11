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

    private String uri = "https://localhost:8445/authenticate";

    @Value("${client-secret}")
    private String apiKey;

    @Autowired
    public AuthenticationApi() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<?> authenticate(String username, String password) {
        HttpEntity<?> entity = usernamePasswordToken(username, password);
        return restTemplate.exchange(uri, HttpMethod.POST, entity, Object.class);
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
