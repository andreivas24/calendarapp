package app.calendar.event.loader;

import app.calendar.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EventLoader implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(String... args) {
    }
}