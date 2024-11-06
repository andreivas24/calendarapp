package app.calendar.event.presentation;

import app.calendar.event.application.EventService;
import app.calendar.event.domain.Event;
import app.calendar.event.presentation.response.EventResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
public class EventResource {
    @Autowired
    private EventService eventService;

    @GetMapping("/default")
    public String defaultTest() {
        return "Working";
    }

    @GetMapping("/")
    public List<EventResponse> getAllEventsInTheNextHour() {
        return eventService.getEventsInNextHour();
    }
}