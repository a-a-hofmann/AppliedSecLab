package ch.ethz.asl.ca.service.event;

/**
 * Event repository in case we decide to persist event as an audit trail.
 */
public interface EventRepository {

    void addEvent(Event event);
}
