package ch.ethz.asl.gateway.endpoint;

import ch.ethz.asl.gateway.dto.AdminReport;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "admin-client", url = "${zuul.routes.resource.url}")
public interface AdminClient {

    @GetMapping("admin")
    AdminReport getAdminReport();

    @DeleteMapping("admin/revoke")
    void revokeAll();
}
