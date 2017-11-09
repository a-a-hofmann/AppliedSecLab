package ch.ethz.asl.ca.service;

import ch.ethz.asl.ca.model.Certificate;
import ch.ethz.asl.ca.model.UserRepository;
import ch.ethz.asl.ca.model.UserSafeProjection;
import ch.ethz.asl.ca.service.command.CertificateManager;
import ch.ethz.asl.ca.service.command.CertificateManagerException;
import ch.ethz.asl.ca.service.event.CertificateEventListener;
import ch.ethz.asl.ca.service.event.CertificateIssuedEvent;
import ch.ethz.asl.ca.service.event.CertificateRequestedEvent;
import ch.ethz.asl.ca.service.event.CertificateRevokedEvent;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.security.Principal;

@Service
public class CertificateService {

    /**
     * To be used to fetch user info for new certs.
     */
    private final UserRepository userRepository;

    private final CertificateManager certificateManager;

    private final CertificateEventListener eventListener;

    public CertificateService(UserRepository userRepository, CertificateManager certificateManager, CertificateEventListener eventListener) {
        this.userRepository = userRepository;
        this.certificateManager = certificateManager;
        this.eventListener = eventListener;
    }

    public boolean getCertificate(final String serialNr, ServletOutputStream outputStream) {
        Certificate certificate = null;
        /*try {
            //certificate = certificateManager.getCertificate(serialNr);

        } catch (CertificateManagerException e) {
            //TODO:
            //how to log, define new EventListener?
            return false;
        }*/

        try {
            IOUtils.copy(certificate, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            //TODO:
            //how to log, define new EventListener?
            return false;
        }

       // eventListener.onCertificateRequested(new CertificateRequestedEvent(serialNr, ));
        return true;
    }



    public boolean issueNewCertificate(final String username) {
        // create cert with user info.
        // Get user info from repo.

        UserSafeProjection user = userRepository.findByUsername(username);
        if (user == null) {
            //how to log, define new EventListener?
            return false;
            //throw new UsernameNotFoundException(username);
        }

        try {
            //serial number
            certificateManager.issueNewCertificate(user);
           // eventListener.onCertificateIssued(new CertificateIssuedEvent(username, cert.getSerialNumber().toString()));
        } catch(CertificateManagerException e) {
            //how to log, define new EventListener?
            return false;
        }
        return true;
    }



    public boolean revokeAllCertsForUser(final String username) {
        //for each certificate -> revoke it
        return false;
    }

    public boolean revokeCertificate(final String serialNr, final Principal principal) {
        boolean success = true;
        try {
            certificateManager.revokeCertificate(serialNr, principal);
            eventListener.onCertificateRevoked(new CertificateRevokedEvent(principal.getName(), serialNr));
        } catch(CertificateManagerException e) {
            // what do I have to do now in Spring? Log this?
            success = false;
        }
        return success;
    }
}
