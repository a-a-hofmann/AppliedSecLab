package ch.ethz.asl.gateway.endpoint;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpServletRequest request) {
        if (isAdmin(request)) {
            return "redirect:/admin";
        }
        return "redirect:/user";
    }

    private boolean isAdmin(HttpServletRequest request) {
        boolean role_admin = request.isUserInRole("ROLE_ADMIN");
        boolean admin = request.isUserInRole("ADMIN");
        return role_admin || admin;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        return "admin";
    }
}
