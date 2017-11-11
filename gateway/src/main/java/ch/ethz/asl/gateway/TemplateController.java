package ch.ethz.asl.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class TemplateController {

    private final UserClient userClient;

    @Autowired
    public TemplateController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/secure")
    public String secure(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());

        User userInfo = userClient.getUserInfo();
        return "secure";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String admin(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        return "admin";
    }
}
