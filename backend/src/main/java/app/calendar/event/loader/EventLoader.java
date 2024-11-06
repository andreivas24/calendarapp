package app.calendar.event.loader;

import app.calendar.event.domain.Event;
import app.calendar.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class EventLoader implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;

    @Override
    public void run(String... args) {
        // Clear existing data to avoid duplication
        eventRepository.deleteAll();

        // Create sample non-periodic events
        Event meeting = Event.builder()
                .title("Team Meeting")
                .description("Monthly team sync")
                .start(LocalDateTime.now().plusMinutes(30))  // Starts 30 minutes from now
                .duration(Duration.ofMinutes(60))  // Lasts for 1 hour
                .periodic(false)
                .build();

        Event webinar = Event.builder()
                .title("Webinar on Spring Boot")
                .description("Learn about Spring Boot fundamentals")
                .start(LocalDateTime.now().plusHours(2))  // Starts 2 hours from now
                .duration(Duration.ofMinutes(90))  // Lasts for 1.5 hours
                .periodic(false)
                .build();

        // Create sample periodic events
        Event dailyStandup = Event.builder()
                .title("Daily Standup")
                .description("Quick team status update")
                .start(LocalDateTime.now().plusMinutes(15))  // Starts 15 minutes from now
                .duration(Duration.ofMinutes(15))  // Lasts for 15 minutes
                .periodic(true)
                .frequency(Duration.ofDays(1))  // Repeats every day
                .build();

        Event weeklyReview = Event.builder()
                .title("Weekly Review")
                .description("Weekly project review")
                .start(LocalDateTime.now().plusDays(1))  // Starts 1 day from now
                .duration(Duration.ofMinutes(120))  // Lasts for 2 hours
                .periodic(true)
                .frequency(Duration.ofDays(7))  // Repeats every week
                .build();

        // Save events to the database
        eventRepository.save(meeting);
        eventRepository.save(webinar);
        eventRepository.save(dailyStandup);
        eventRepository.save(weeklyReview);

        // Print a message to confirm data loading
        System.out.println("Sample events loaded into the database.");
    }
}