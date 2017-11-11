package ch.ethz.asl.gateway;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class TemplateController {

    @GetMapping("/")
    public String index() {
        return "redirect:/user";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        return "admin";
    }
}
