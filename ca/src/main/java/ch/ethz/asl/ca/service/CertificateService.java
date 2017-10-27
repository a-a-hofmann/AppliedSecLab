package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

    private final UserRepository userRepository;

    /**
     * Wrapper to execute
     */
    private final ShellExecutionWrapper shellExecutionWrapper;

    public CertificateService(UserRepository userRepository, ShellExecutionWrapper shellExecutionWrapper) {
        this.userRepository = userRepository;
        this.shellExecutionWrapper = shellExecutionWrapper;
    }

    public void getCertificate(final String serialNr) {
        
    }

    public void issueNewCertificate(final String username) {
        // create cert with user info.
        // Get user info from repo.
        shellExecutionWrapper.doStuff();
    }
}
