package ch.ethz.asl.ca.service.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a generic event in our system.
 */
public abstract class Event {

    private final String id;

    /**
     * The username of the user that triggered the event.
     */
    private final String user;

    /**
     * When this event was triggered.
     */
    private final LocalDateTime localDateTime;

    Event(String user) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.localDateTime = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return id != null ? id.equals(event.id) : event.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", localDateTime=" + localDateTime +
                '}';
    }
}
