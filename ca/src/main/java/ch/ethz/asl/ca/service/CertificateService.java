package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.User;
import ch.ethz.asl.ca.model.UserCertificate;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.service.command.CertificateManager;
import ch.ethz.asl.ca.service.command.CertificateManagerException;
import ch.ethz.asl.ca.service.event.CertificateEventListener;
import ch.ethz.asl.ca.service.event.CertificateIssuedEvent;
import ch.ethz.asl.ca.service.event.CertificateRequestedEvent;
import ch.ethz.asl.ca.service.event.CertificateRevokedEvent;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    //TODO:Logging
    private static final Logger logger = Logger.getLogger(CertificateService.class);

    /**
     * To be used to fetch user info for new certs.
     */
    private final UserRepository userRepository;

    private final CertificateManager certificateManager;

    private final CertificateEventListener eventListener;

    private final UserCertificateService userCertificateService;

    //TODO: Here UserCertificateService
    public CertificateService(UserRepository userRepository, CertificateManager certificateManager, CertificateEventListener eventListener, UserCertificateService userCertificateService) {

        this.userRepository = userRepository;
        this.certificateManager = certificateManager;
        this.eventListener = eventListener;
        this.userCertificateService = userCertificateService;
    }

    public List<UserCertificate> getUserCertificates(final String username) {
        User user = userRepository.findOne(username);
        return getUserCertificates(user);
    }

    private List<UserCertificate> getUserCertificates(final User user) {
        return userCertificateService.findAllByUser(user);
    }

    public List<UserCertificate> getAllRevokedCertificates() {
        return userCertificateService.findAllRevoked();
    }

    public byte[] getCertificate(final String serialNr, final String username) {

        User user = userRepository.findOne(username);
        if (user == null) {
            throw new IllegalArgumentException(String.format("No user found in security context. Searched for username [%s]", username));
        }

        eventListener.onCertificateRequested(new CertificateRequestedEvent(user.getUsername(), serialNr));

        UserCertificate userCertificate;

        Optional<UserCertificate> certificateOptional = userCertificateService.findBySerialNrAndUser(serialNr, user);
        if (certificateOptional.isPresent()) {
            userCertificate = certificateOptional.get();
        } else {
            throw new IllegalArgumentException(String.format("Failed to get certificate [%s] for user [%s]", serialNr, user.getUsername()));
        }

        byte[] success;
        try {
            success = certificateManager.getCertificate(userCertificate.getSerialNr(), user);
            //successfully got the certificate -> log it.
        } catch (CertificateManagerException e) {
            throw new IllegalArgumentException("Something went wrong while fetching your cert.", e);
        }

        return success;
    }


    public boolean issueNewCertificate(final String username) {

        User user = userRepository.findOne(username);
        if (user == null) {
            //log user doesn't exist -> should not happen normally
            return false;
        }

        eventListener.onCertificateIssued(new CertificateIssuedEvent(username));

        String serialNr = null;
        try {
            serialNr = certificateManager.issueNewCertificate(user);
            UserCertificate userCertificate = userCertificateService.issueCertificateForUser(user, serialNr, certificateManager.getPath(serialNr, user));
            //successfully issued the certificate -> Log it
        } catch (CertificateManagerException e) {
            logger.error(String.format("Failed to issue certificate to user [%s]", user.getUsername()), e);
            return false;
        }
        return true;
    }


    public boolean revokeAllCertsForUser(final String username) {
        User user = userRepository.findOne(username);
        if (user == null) {
            //log user doesn't exist -> should not happen normally
            return false;
        }

        List<UserCertificate> certificateList = userCertificateService.findAllByUserNotRevoked(user);
        boolean success = true;
        for (UserCertificate certificate : certificateList) {
            success &= revokeCertificate(certificate.getSerialNr(), username);
        }
        return success;
    }

    public boolean revokeCertificate(final String serialNr, final String username) {
        User user = userRepository.findOne(username);
        if (user == null) {
            //log this -> should not happen normally
            return false;
        }

        eventListener.onCertificateRevoked(new CertificateRevokedEvent(user.getUsername(), serialNr));

        boolean success = false;
        try {
            success = certificateManager.revokeCertificate(serialNr, user);

            UserCertificate certificate = userCertificateService.revokeCertificate(user, serialNr);
            logger.info(String.format("Revoked certificate [%s] on %s", certificate.getSerialNr(), certificate.getRevokedOn()));

            //successfully revoked the certificate -> Log it
        } catch (CertificateManagerException e) {
            logger.error(String.format("Failed to revoke certificate [%s] for user [%s]", serialNr, user.getUsername()), e);
            return false;
        }
        return success;
    }

    public Long getNumberOfIssuedCertificates() {
        Long numberOfIssuedCertificates = null;
        try {
            numberOfIssuedCertificates = certificateManager.getNumberOfIssuedCertificates();
        } catch (CertificateManagerException e) {
            //log this, should not happen normally.
        }
        return numberOfIssuedCertificates;
    }

    public Long getNumberOfRevokedCertificates() {
        Long numberOfRevokedCertificates = null;
        try {
            return certificateManager.getNumberOfRevokedCertificates();
        } catch (CertificateManagerException e) {
            //log this, should not happen normally.
        }
        return numberOfRevokedCertificates;
    }

    public String getCurrentSerialNumber() {
        String currentSerialNr = null;
        try {
            currentSerialNr = certificateManager.getCurrentSerialNumber();
        } catch (CertificateManagerException e) {
            //log this, should not happen normally.
        }
        return currentSerialNr;
    }

    public byte[] getCrl() {
        try {
            return certificateManager.getCrl();
        } catch (CertificateManagerException e) {
            logger.error("Failed to fetch CRL file.", e);
        }
        return null;
    }
}
