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
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
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

    /**
     * Mock implementation. Use db.
     *
     * @param username
     * @return
     */
    public List<UserCertificate> getUserCertificates(final String username) {
        String serialNr = "0";
        String serialNr2 = "1";
        String serialNr3 = "2";
        String serialNr4 = "3";
        User user = userRepository.findOne(username);
        UserCertificate c1 = UserCertificate.issuedNowToUser(serialNr, "etc/ssl/CA/newcerts/db/0.pem", user);
        UserCertificate c2 = UserCertificate.issuedNowToUser(serialNr2,"etc/ssl/CA/newcerts/db/1.pem", user);
        UserCertificate c3 = UserCertificate.issuedNowToUser(serialNr3, "etc/ssl/CA/newcerts/db/2.pem", user);
        UserCertificate c4 = UserCertificate.issuedNowToUser(serialNr4,"etc/ssl/CA/newcerts/db/3.pem", user);
        c3.revoke();

        // TODO Use db once ca is done
        // getUserCertificates(user);

        // Use mock instead.
        return Lists.newArrayList(c1, c2, c3, c4);
    }

    private List<UserCertificate> getUserCertificates(final User user) {
        return userCertificateService.findAllByUser(user);
    }

    public boolean getCertificate(final String serialNr, final String username, ServletOutputStream outputStream) {

        User user = userRepository.findOne(username);
        if (user == null) {
            //log this -> should not happen normally
            return false;
        }

        eventListener.onCertificateRequested(new CertificateRequestedEvent(user.getUsername(), serialNr));

        UserCertificate userCertificate = null;

        Optional<UserCertificate> certificateOptional = userCertificateService.findBySerialNrAndUser(serialNr, user);
        if(certificateOptional.isPresent()) {
                userCertificate = certificateOptional.get();
        } else {
            //log not found combination user + serialNr.
            logger.error(String.format("Failed to get certificate [%s] for user [%s]", serialNr, user.getUsername()));
            return false;
        }

        boolean success;
        try {
           success = certificateManager.getCertificate(userCertificate.getSerialNr(), user, outputStream);
            //successfully got the certificate -> log it.
        } catch (CertificateManagerException e) {
            //log this -> should not happen normally
            return false;
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
        for(UserCertificate certificate : certificateList) {
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
        return  numberOfIssuedCertificates;
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
}
