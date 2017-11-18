package ch.ethz.asl.gateway.endpoint;

import ch.ethz.asl.gateway.dto.AdminReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final AdminClient adminClient;

    @Autowired
    public AdminController(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @GetMapping("/admin")
    public String admin(Principal principal, Model model) {
        AdminReport adminReport = adminClient.getAdminReport();
        model.addAttribute("user", principal.getName());
        model.addAttribute("adminReport", adminReport);
        return "admin";
    }
}
