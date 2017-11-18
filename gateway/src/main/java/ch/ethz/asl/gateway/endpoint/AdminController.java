package ch.ethz.asl.gateway.endpoint;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @GetMapping("/admin")
    public String admin(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        return "admin";
    }
}
