package app.calendar.event.presentation;

import app.calendar.event.application.EventService;
import app.calendar.event.domain.Event;
import app.calendar.event.presentation.authorization.UserOwnsEvent;
import app.calendar.event.presentation.exception.BadRequestException;
import app.calendar.event.presentation.exception.EventNotFoundException;
import app.calendar.event.presentation.response.EventInfo;
import app.calendar.event.presentation.response.EventBlob;
import app.calendar.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/event")
public class EventResource {
    @Autowired
    private EventService eventService;

    @GetMapping("/")
    public List<EventBlob> getEventsInInterval(
            @RequestParam("start") LocalDateTime start,
            @RequestParam("end") LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Start time cannot be after end time.");
        }
        return eventService.getEventsInInterval(start, end).stream().map(EventBlob::mapFromEvent).collect(Collectors.toList());
    }

    @PostMapping("/")
    public ResponseEntity<EventInfo> createEvent(@RequestBody Event event) {
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            throw new BadRequestException("Event title is required.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();

        event.setUser(authenticatedUser);

        Event createdEvent = eventService.createEvent(event);
        EventInfo eventInfo = EventInfo.mapFromEvent(createdEvent);

        return new ResponseEntity<>(eventInfo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @UserOwnsEvent
    public ResponseEntity<EventInfo> getEventById(@PathVariable Long id) {
        Optional<Event> eventBlob = eventService.getEventById(id);
        return eventBlob
                .map(event -> new ResponseEntity<>(EventInfo.mapFromEvent(event), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    @UserOwnsEvent
    public ResponseEntity<EventInfo> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        Event updatedEvent = eventService.updateEvent(id, eventDetails)
                .orElseThrow(() -> new EventNotFoundException(id));
        EventInfo eventInfo = EventInfo.mapFromEvent(updatedEvent);
        return new ResponseEntity<>(eventInfo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @UserOwnsEvent
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        boolean deleted = eventService.deleteEvent(id);
        return deleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/upcoming")
    public List<EventBlob> getAllEventsInTheNextHour() {
        return eventService.getEventsInInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(1)).stream().map(EventBlob::mapFromEvent).collect(Collectors.toList());
    }
}