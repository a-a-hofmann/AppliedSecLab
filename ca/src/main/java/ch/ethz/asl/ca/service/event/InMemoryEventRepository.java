package ch.ethz.asl.ca.service.event;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * In-memory implementation of {@link EventRepository}.
 * It simply saved events into a LinkedHashSet to avoid duplicates but maintain insertion order.
 */
@Component
public class InMemoryEventRepository implements EventRepository {

    private Set<Event> events = new LinkedHashSet<>();

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }
}
