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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final UserClient userClient;

    private final CertificateClient certificateClient;

    @Autowired
    public UserController(UserClient userClient, CertificateClient certificateClient) {
        this.userClient = userClient;
        this.certificateClient = certificateClient;
    }

    @GetMapping("user")
    public String getUser(Model model) {
        User userInfo = userClient.getUserInfo();
        model.addAttribute("user", userInfo);
        fillModelWithCertificates(certificateClient.getUserCertificates(), model);
        return "user";
    }

    @PostMapping("user")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            fillModelWithCertificates(certificateClient.getUserCertificates(), model);
            return "user";
        }

        user.setUsername(principal.getName());
        userClient.save(user);
        return "redirect:/user";
    }

    @PostMapping("cert")
    public String requestCertificate(Principal principal) {
        logger.info(String.format("Requesting new certificate for user [%s]", principal.getName()));
        certificateClient.requestCertificate();
        return "redirect:/user";
    }


    private void fillModelWithCertificates(List<UserCertificate> certificates, Model model) {
        List<UserCertificate> revokedCerts = certificates.stream().filter(UserCertificate::isRevoked).collect(Collectors.toList());
        certificates = certificates.stream().filter(c -> !c.isRevoked()).collect(Collectors.toList());

        Collections.sort(certificates);
        Collections.sort(revokedCerts);

        model.addAttribute("certificates", certificates);
        model.addAttribute("revokedCerts", revokedCerts);
    }
}
