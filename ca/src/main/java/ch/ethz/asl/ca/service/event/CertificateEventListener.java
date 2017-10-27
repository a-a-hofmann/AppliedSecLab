package ch.ethz.asl.ca.service.event;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Event listener for events regarding certificates.
 */
@Component
public class CertificateEventListener {

    private static final Logger logger = Logger.getLogger(CertificateEventListener.class);

    private final EventRepository eventRepository;

    public CertificateEventListener(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void onCertificateIssued(CertificateIssuedEvent event) {
        eventRepository.addEvent(event);
    }

    public void onCertificateRevoked(CertificateRevokedEvent event) {
        eventRepository.addEvent(event);
    }
}
