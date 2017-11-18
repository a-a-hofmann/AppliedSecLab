package ch.ethz.asl.gateway.endpoint;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

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
}
