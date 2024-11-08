package app.calendar.event.application;

import app.calendar.event.domain.Event;
import app.calendar.event.presentation.exception.BadRequestException;
import app.calendar.event.presentation.response.EventBlob;
import app.calendar.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getEventsInInterval(LocalDateTime start, LocalDateTime end) {
        List<Event> allEvents = eventRepository.findAll();
        List<Event> eventBlobsInInterval = new ArrayList<>();

        for (Event event : allEvents) {
            if (!event.isPeriodic()) {
                if (isEventInInterval(event.getStart(), event.getDuration(), start, end)) {
                    eventBlobsInInterval.add(event);
                }
            } else {
                addRecurringEventOccurrences(event, start, end, eventBlobsInInterval);
            }
        }
        return eventBlobsInInterval;
    }

    private boolean isEventInInterval(LocalDateTime eventStart, Duration duration, LocalDateTime intervalStart, LocalDateTime intervalEnd) {
        LocalDateTime eventEnd = eventStart.plus(duration);
        return eventStart.isBefore(intervalEnd) && eventEnd.isAfter(intervalStart);
    }

    private void addRecurringEventOccurrences(Event event, LocalDateTime intervalStart, LocalDateTime intervalEnd, List<Event> eventBlobsInInterval) {
        LocalDateTime nextOccurrence = event.getStart();

        while (nextOccurrence.isBefore(intervalEnd)) {
            if (isEventInInterval(nextOccurrence, event.getDuration(), intervalStart, intervalEnd)) {
                eventBlobsInInterval.add(event);
            }
            nextOccurrence = nextOccurrence.plus(event.getFrequency());
        }
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Optional<Event> updateEvent(Long id, Event eventDetails) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(eventDetails.getTitle());
            event.setDescription(eventDetails.getDescription());
            event.setStart(eventDetails.getStart());
            event.setDuration(eventDetails.getDuration());
            event.setPeriodic(eventDetails.isPeriodic());
            event.setFrequency(eventDetails.getFrequency());
            return eventRepository.save(event);
        });
    }

    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
