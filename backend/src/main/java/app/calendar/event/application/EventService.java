package app.calendar.event.application;

import app.calendar.event.domain.Event;
import app.calendar.event.presentation.response.EventResponse;
import app.calendar.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<EventResponse> getEventsInNextHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<Event> upcomingEvents = retrieveUpcomingEvents(now, oneHourLater);
        return upcomingEvents.stream()
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());
    }

    private List<Event> retrieveUpcomingEvents(LocalDateTime now, LocalDateTime oneHourLater) {
        List<Event> allEvents = eventRepository.findAll();
        List<Event> upcomingEvents = new ArrayList<>();

        for (Event event : allEvents) {
            if (!event.isPeriodic()) {
                if (isEventInNextHour(event.getStart(), event.getDuration(), now, oneHourLater)) {
                    upcomingEvents.add(event);
                }
            } else {
                addRecurringEventOccurrences(event, now, oneHourLater, upcomingEvents);
            }
        }
        return upcomingEvents;
    }

    private boolean isEventInNextHour(LocalDateTime start, Duration duration, LocalDateTime now, LocalDateTime oneHourLater) {
        LocalDateTime eventEnd = start.plus(duration);
        return (start.isBefore(oneHourLater) && eventEnd.isAfter(now));
    }

    private void addRecurringEventOccurrences(Event event, LocalDateTime now, LocalDateTime oneHourLater, List<Event> upcomingEvents) {
        LocalDateTime nextOccurrence = event.getStart();

        while (nextOccurrence.isBefore(oneHourLater)) {
            if (isEventInNextHour(nextOccurrence, event.getDuration(), now, oneHourLater)) {
                upcomingEvents.add(createEventOccurrence(event, nextOccurrence));
            }
            nextOccurrence = nextOccurrence.plus(event.getFrequency());
        }
    }

    private Event createEventOccurrence(Event originalEvent, LocalDateTime occurrenceTime) {
        return Event.builder()
                .id(originalEvent.getId())
                .title(originalEvent.getTitle())
                .description(originalEvent.getDescription())
                .start(occurrenceTime)
                .duration(originalEvent.getDuration())
                .periodic(false)
                .build();
    }

    private EventResponse mapToEventResponse(Event event) {
        return new EventResponse(
                event.getTitle(),
                event.getDescription(),
                event.getStart(),
                event.getDuration()
        );
    }
}
