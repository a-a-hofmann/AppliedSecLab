package ch.ethz.asl.gateway.endpoint;

import ch.ethz.asl.gateway.dto.CertificateRevocationCommand;
import ch.ethz.asl.gateway.dto.User;
import ch.ethz.asl.gateway.dto.UserCertificate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
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
        fillModelWithCertificates(certificateClient.getUserCertificates(), certificateClient.getAllRevoked(), model);
        return "user";
    }

    @PostMapping("user")
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            fillModelWithCertificates(certificateClient.getUserCertificates(), certificateClient.getAllRevoked(), model);
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

    @GetMapping("cert/{serialNr}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable("serialNr") String serialNr) throws IOException {
        ResponseEntity<byte[]> certificate = certificateClient.downloadCertificate(serialNr);
        if (!certificate.getStatusCode().equals(HttpStatus.OK) || certificate.getBody() == null) {
            return ResponseEntity.status(certificate.getStatusCode()).build();
        }

        ByteArrayResource resource = new ByteArrayResource(certificate.getBody());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + serialNr + ".p12")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping("cert/revoke")
    public String revokeCertificate(CertificateRevocationCommand revoke, Principal principal) {
        // TODO send hashed pwd to ca for revocation check.. or not and remove it from UI.
        logger.info(String.format("Revoking certificate [%s] for user [%s]", revoke.getSerialNr(), principal.getName()));
        ResponseEntity<Void> responseEntity = certificateClient.revokeCertificate(revoke.getSerialNr());
        logger.info(responseEntity.getStatusCode());
        return "redirect:/user";
    }

    @PostMapping("crl")
    public ResponseEntity<Resource> downloadCrl() {
        ResponseEntity<ByteArrayResource> response = certificateClient.downloadCrl();
        if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=crl.pem")
                .contentLength(response.getBody().contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(response.getBody());
    }


    private void fillModelWithCertificates(List<UserCertificate> certificates, List<UserCertificate> allRevokedCerts, Model model) {
        List<UserCertificate> revokedCerts = certificates.stream().filter(UserCertificate::isRevoked).collect(Collectors.toList());
        certificates = certificates.stream().filter(c -> !c.isRevoked()).collect(Collectors.toList());

        Collections.sort(certificates);
        Collections.sort(revokedCerts);

        model.addAttribute("certificates", certificates);
        model.addAttribute("revokedCerts", revokedCerts);
        model.addAttribute("allRevokedCerts", allRevokedCerts);
    }
}
