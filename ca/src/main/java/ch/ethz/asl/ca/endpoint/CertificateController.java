package ch.ethz.asl.ca.endpoint;

import ch.ethz.asl.ca.model.AdminReport;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.CertificateService;
import ch.ethz.asl.ca.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

/**
 * Controller to answer to requests regarding certificates.
 */
@RestController
public class CertificateController {

    private static final Logger logger = Logger.getLogger(CertificateController.class);

    private final CertificateService certificateService;

    private final UserService userService;

    @Autowired
    public CertificateController(CertificateService certificateService, UserService userService) {
        this.certificateService = certificateService;
        this.userService = userService;
    }

    @GetMapping("cert")
    public List<UserCertificate> getUserCertificates(Principal principal, HttpServletRequest request) {
        if (isAdmin(request)) {
            return certificateService.getAllCertificates();
        }
        return certificateService.getUserCertificates(principal.getName());
    }

    private boolean isAdmin(HttpServletRequest request) {
        boolean role_admin = request.isUserInRole("ROLE_ADMIN");
        boolean admin = request.isUserInRole("ADMIN");
        return role_admin || admin;
    }

    @GetMapping("cert/{serialNr}")
    public ResponseEntity<byte[]> getCertificate(@PathVariable("serialNr") final String serialNr, Principal principal) {
        byte[] certificate = certificateService.getCertificate(serialNr, principal.getName());
        if (certificate == null || certificate.length == 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(certificate);
    }

    @PostMapping("authenticate/cert")
    public ResponseEntity<String> verifyCertificate(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authorization)) {
            return ResponseEntity.badRequest().build();
        }

        CredentialsParser.BasicAuthComponents userCredentials = new CredentialsParser.BasicAuthParser().parse(authorization);
        final String email = userCredentials.getUsername();
        final String serialNr = userCredentials.getPassword();

        UserSafeProjection user = userService.findUsernameByEmail(email);
        boolean certificateRevoked = certificateService.isCertificateRevoked(serialNr);

        if (user != null && !certificateRevoked) {
            return ResponseEntity.ok(user.getUsername());
        }
        return ResponseEntity.ok().build();
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public AdminReport getAdminReport() {
        Long numberOfIssuedCertificates = certificateService.getNumberOfIssuedCertificates();
        Long numberOfRevokedCertificates = certificateService.getNumberOfRevokedCertificates();
        String currentSerialNumber = certificateService.getCurrentSerialNumber();
        return new AdminReport(numberOfIssuedCertificates, numberOfRevokedCertificates, currentSerialNumber);
    }
}
