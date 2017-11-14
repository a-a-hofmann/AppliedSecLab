package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.service.CertificateService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller to answer to requests regarding certificates.
 */
@RestController
public class CertificateController {

    private static final Logger logger = Logger.getLogger(CertificateController.class);

    private final CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("cert")
    public List<UserCertificate> getUserCertificates(Principal principal) {
        return certificateService.getUserCertificates(principal.getName());
    }

    @GetMapping("cert/{serialNr}")
    public ResponseEntity<byte[]> getCertificate(@PathVariable("serialNr") final String serialNr, Principal principal) {
        byte[] certificate = certificateService.getCertificate(serialNr, principal.getName());
        if (certificate == null || certificate.length == 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(certificate);
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

    @GetMapping("crl")
    public ResponseEntity<ByteArrayResource> downloadCrl(Principal principal) {
        logger.info(String.format("User [%s] request CRL file.", principal.getName()));
        byte[] crl = certificateService.getCrl();
        if (crl != null) {
            return ResponseEntity.ok(new ByteArrayResource(crl));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("revoked")
    public List<UserCertificate> getAllRevokedCerts() {
        return certificateService.getAllRevokedCertificates();
    }

    @GetMapping("admin")
    public void getAdminReport() {
        // how do you implement this as endpoint?
    }
}
