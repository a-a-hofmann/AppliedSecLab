package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public void getCertificate(@PathVariable("serialNr") final String serialNr) {
        certificateService.getCertificate(serialNr);
    }

    @PostMapping("cert/{username}")
    public void issueNewCertificate(@PathVariable("username") final String username) {
        certificateService.issueNewCertificate(username);
    }

    @DeleteMapping("cert/{serialNr}")
    public boolean deleteCertificate(@PathVariable("serialNr") final String serialNr) {
        return false;
    }

    @GetMapping("admin")
    public void getAdminReport() {

    }
}
