package app.calendar.event.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/event")
public class EventResource {
    @GetMapping("/default")
    public String defaultTest() {
        return "Working";
    }
}