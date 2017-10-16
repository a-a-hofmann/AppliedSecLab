package com.example.gateway;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class TemplateController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/secure")
    public String secure(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        return "secure";
    }
}
