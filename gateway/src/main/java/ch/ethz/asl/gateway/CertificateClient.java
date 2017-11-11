package ch.ethz.asl.gateway;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value = "cert-client", url = "${zuul.routes.resource.url}")
public interface CertificateClient {

    @GetMapping("cert")
    List<UserCertificate> getUserCertificates();

    @PostMapping("cert")
    Void requestCertificate();
}
