package ch.ethz.asl.gateway;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "user-client", url = "${zuul.routes.resource.url}")
public interface UserClient {

    @GetMapping("user")
    User getUserInfo();

    @PostMapping("user")
    User save(User user);

}
