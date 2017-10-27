package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.service.event.CertificateEventListener;
import ch.ethz.asl.ca.service.event.CertificateRevokedEvent;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CertificateService {

    /**
     * To be used to fetch user info for new certs.
     */
    private final UserRepository userRepository;

    /**
     * Wrapper to execute
     */
    private final ShellExecutionWrapper shellExecutionWrapper;

    private final CertificateEventListener eventListener;

    public CertificateService(UserRepository userRepository, ShellExecutionWrapper shellExecutionWrapper, CertificateEventListener eventListener) {
        this.userRepository = userRepository;
        this.shellExecutionWrapper = shellExecutionWrapper;
        this.eventListener = eventListener;
    }

    public void getCertificate(final String serialNr) {

    }

    public void issueNewCertificate(final String username) {
        // create cert with user info.
        // Get user info from repo.
        shellExecutionWrapper.doStuff();
    }

    public boolean revokeAllCertsForUser(final String username) {
        return false;
    }

    public boolean revokeCertificate(final String serialNr, final Principal principal) {
        eventListener.onCertificateRevoked(new CertificateRevokedEvent(principal.getName(), serialNr));
        return false;
    }
}
