package ch.ethz.asl.ca.service.event;

import ch.ethz.asl.ca.service.CertificateService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Event listener for events regarding user data.
 */
@Component
public class UserEventListener {

    private static final Logger logger = Logger.getLogger(UserEventListener.class);

    private final EventRepository eventRepository;

    private final CertificateService certificateService;

    public UserEventListener(EventRepository eventRepository, CertificateService certificateService) {
        this.eventRepository = eventRepository;
        this.certificateService = certificateService;
    }

    public void onUserInfoRequest(UserInfoRequestEvent event) {
        eventRepository.addEvent(event);
    }

    public void onUserInfoUpdate(UserDetailsUpdateEvent event) {
        eventRepository.addEvent(event);

        if (event.isRevokeCerts()) {
            certificateService.revokeAllCertsForUser(event.getUser());
        }
    }
}
