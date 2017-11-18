package ch.ethz.asl.gateway.endpoint;

import ch.ethz.asl.gateway.dto.AdminReport;
import ch.ethz.asl.gateway.dto.UserCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminClient adminClient;

    private final CertificateClient certificateClient;

    @Autowired
    public AdminController(AdminClient adminClient, CertificateClient certificateClient) {
        this.adminClient = adminClient;
        this.certificateClient = certificateClient;
    }

    @GetMapping("/admin")
    public String admin(Principal principal, Model model) {
        AdminReport adminReport = adminClient.getAdminReport();
        List<UserCertificate> validCertificates = certificateClient.getUserCertificates().stream().filter(cert -> !cert.isRevoked()).collect(Collectors.toList());
        model.addAttribute("user", principal.getName());
        model.addAttribute("adminReport", adminReport);
        model.addAttribute("certificates", validCertificates);
        model.addAttribute("allRevokedCerts", certificateClient.getAllRevoked());
        return "admin";
    }

    @PostMapping("/admin/revoke")
    public String revokeAll() {
        adminClient.revokeAll();
        return "redirect:/admin";
    }
}
