package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * Controller to answer to requests regarding certificates.
 */
@RestController
public class CertificateController {

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("cert/{serialNr}")
    public ResponseEntity<Void> getCertificate(@PathVariable("serialNr") final String serialNr, Principal principal, HttpServletResponse response) {
        boolean success = false;
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            success =  certificateService.getCertificate(serialNr, principal.getName(), outputStream);
        } catch (IOException e) {
            //TODO:
            e.printStackTrace();
        }

        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();

    }

    @PostMapping("cert")
    public ResponseEntity<Void> issueNewCertificate(Principal principal) {

        boolean success = certificateService.issueNewCertificate(principal.getName());
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("cert/{serialNr}")
    public ResponseEntity<Void> revokeCertificate(@PathVariable("serialNr") final String serialNr, Principal principal) {
        boolean success = certificateService.revokeCertificate(serialNr, principal.getName());
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("admin")
    public void getAdminReport() {
        // how do you implement this as endpoint?
    }
}
