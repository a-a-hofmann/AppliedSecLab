package ch.ethz.asl.gateway;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "cert-client", url = "${zuul.routes.resource.url}")
public interface CertificateClient {

    @GetMapping("cert")
    List<UserCertificate> getUserCertificates();

    @PostMapping("cert")
    Void requestCertificate();

    @GetMapping("cert/{serialNr}")
    ResponseEntity<byte[]> downloadCertificate(@PathVariable("serialNr") String serialNr);

    @GetMapping("crl")
    ResponseEntity<ByteArrayResource> downloadCrl();

    @GetMapping("revoked")
    List<UserCertificate> getAllRevoked();

    @DeleteMapping("cert/{serialNr}")
    ResponseEntity<Void> revokeCertificate(@PathVariable("serialNr") String serialNr);
}
