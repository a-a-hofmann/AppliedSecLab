package ch.ethz.asl.auth.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.cert.X509Certificate;

@Component
public class CRLValidityService {

    private final RestTemplate restTemplate;

    public CRLValidityService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean isCertRevoked(X509Certificate certificate) {
//        BigInteger serialNumber = certificate.getSerialNumber();
//        RequestEntity<Void> requestEntity = RequestEntity.get(URI.create("https://localhost:8443/cert/" + serialNumber)).build(); // TODO: requires ssl certificate of CRL server to be imported in cacerts of jre for this server.
//        ResponseEntity<Void> response = restTemplate.exchange(requestEntity, Void.class);
//        HttpStatus statusCode = response.getStatusCode();
//        return !statusCode.is2xxSuccessful();
        return false;
    }
}
