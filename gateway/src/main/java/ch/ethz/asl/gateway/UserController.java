package ch.ethz.asl.gateway;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping("user")
    public String getUser(Model model) {
        User userInfo = userClient.getUserInfo();
        model.addAttribute("user", userInfo);
        return "user";
    }

    @PostMapping("user")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("formError", true);
            model.addAttribute("errors", bindingResult.getFieldErrors());
            return "user";
        }

        user.setUsername(principal.getName());
        userClient.save(user);
        return "redirect:/user";
    }
}
