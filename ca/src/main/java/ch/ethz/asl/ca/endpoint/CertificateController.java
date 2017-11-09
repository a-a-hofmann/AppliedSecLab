package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Controller to answer to requests regarding certificates.
 * TODO: once login is in place remove the need to pass the username in HTTP body and read user info from SecurityContext.
 */
@RestController
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("cert/{serialNr}")
    public void getCertificate(@PathVariable("serialNr") final String serialNr, Principal principal, HttpServletResponse response) {
        try (ServletOutputStream inputStream = response.getOutputStream()) {
            String username = principal.getName();
            certificateService.getCertificate(serialNr, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("cert/{username}")
    public ResponseEntity<Void> issueNewCertificate(@PathVariable("username") final String username) {
        boolean success = certificateService.issueNewCertificate(username);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("cert/{serialNr}")
    public boolean revokeCertificate(@PathVariable("serialNr") final String serialNr, Principal principal) {
        return certificateService.revokeCertificate(serialNr, principal);
    }

    @GetMapping("admin")
    public void getAdminReport() {

    }
}
