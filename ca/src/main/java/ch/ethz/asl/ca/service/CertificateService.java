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
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {
    private static final Logger logger = Logger.getLogger(CertificateService.class);

    /**
     * To be used to fetch user info for new certs.
     */
    private final UserRepository userRepository;

    private final CertificateManager certificateManager;

    private final CertificateEventListener eventListener;

    private final UserCertificateService userCertificateService;

    public CertificateService(UserRepository userRepository, CertificateManager certificateManager, CertificateEventListener eventListener, UserCertificateService userCertificateService) {
        this.userRepository = userRepository;
        this.certificateManager = certificateManager;
        this.eventListener = eventListener;
        this.userCertificateService = userCertificateService;
    }

    public List<UserCertificate> getUserCertificates(final String username) {
        User user = getUser(username);
        return getUserCertificates(user);
    }

    private List<UserCertificate> getUserCertificates(final User user) {
        return userCertificateService.findAllByUser(user);
    }

    public List<UserCertificate> getAllRevokedCertificates() {
        return userCertificateService.findAllRevoked();
    }

    public byte[] getCertificate(final String serialNr, final String username) {
        User user = getUser(username);

        UserCertificate userCertificate;

        Optional<UserCertificate> certificateOptional = userCertificateService.findBySerialNrAndUser(serialNr, user);
        if (certificateOptional.isPresent()) {
            userCertificate = certificateOptional.get();
        } else {
            throw new IllegalArgumentException(String.format("Failed to get certificate [%s] for user [%s]", serialNr, user.getUsername()));
        }

        if (userCertificate.isRevoked()) {
            logger.info(String.format("User [%s] requested certificate [%s] that was revoked.", user.getUsername(), serialNr));
            return new byte[0];
        }

        byte[] success;
        try {
            success = certificateManager.getCertificate(userCertificate.getSerialNr(), user);
            eventListener.onCertificateRequested(new CertificateRequestedEvent(user.getUsername(), serialNr));
        } catch (CertificateManagerException e) {
            throw new IllegalArgumentException("Something went wrong while fetching your cert.", e);
        }

        return success;
    }


    public boolean issueNewCertificate(final String username) {
        User user = getUser(username);

        try {
            String serialNr = certificateManager.issueNewCertificate(user);
            UserCertificate userCertificate = userCertificateService.issueCertificateForUser(user, serialNr, certificateManager.getPath(serialNr, user));
            eventListener.onCertificateIssued(new CertificateIssuedEvent(username, userCertificate.getSerialNr()));
        } catch (CertificateManagerException e) {
            logger.error(String.format("Failed to issue certificate to user [%s]", user.getUsername()), e);
            return false;
        }
        return true;
    }

    public boolean revokeAllCertsForUser(final String username) {
        User user = getUser(username);

        List<UserCertificate> certificateList = userCertificateService.findAllByUserNotRevoked(user);
        boolean success = true;
        for (UserCertificate certificate : certificateList) {
            success &= revokeCertificate(certificate.getSerialNr(), username);
        }
        return success;
    }

    public boolean revokeCertificate(final String serialNr, final String username) {
        User user = getUser(username);

        eventListener.onCertificateRevoked(new CertificateRevokedEvent(user.getUsername(), serialNr));

        boolean success;
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
            long numberOfCertificatesInDb = userCertificateService.countNumberOfCertificates();

            if (numberOfCertificatesInDb != numberOfIssuedCertificates) {
                logger.warn(String.format("Certificates in DB and in serial number do not match... DB=[%d], serialNr=[%s]", numberOfCertificatesInDb, numberOfIssuedCertificates));
            }
        } catch (CertificateManagerException e) {
            logger.error("Failed to get number of certificates.", e);
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

    public User getUser(final String username) {
        User user = userRepository.findOne(username);
        if (user == null) {
            throw new AuthenticationServiceException("User not found: " + username);
        }
        return user;
    }

    public boolean isCertificateRevoked(final String serialNr) {
        return userCertificateService.isCertificateRevoked(serialNr);
    }
}
