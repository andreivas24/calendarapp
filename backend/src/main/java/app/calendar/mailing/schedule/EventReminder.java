package app.calendar.mailing.schedule;

import app.calendar.event.application.EventService;
import app.calendar.event.domain.Event;
import app.calendar.mailing.application.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Component responsible for sending event reminders.
 */
@Component
public class EventReminder {

    @Autowired
    private MailService mailService;

    @Autowired
    private EventService eventService;

    /**
     * Scheduled task that sends reminders for upcoming events every hour.
     */
    @Scheduled(fixedRate = 3600000)
    public void sendEventReminders() {
        List<Event> events = eventService.getEventsInInterval(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        HashMap<String, List<Event>> eventsByUser = new HashMap<>();
        for (Event e : events) {
            eventsByUser.computeIfAbsent(e.getUser().getEmail(), k -> new ArrayList<>()).add(e);
        }

        for (String email : eventsByUser.keySet()) {
            String content = generateEmailContent(eventsByUser.get(email));
            mailService.sendReminder(email, "Upcoming events", content);
        }
    }

    /**
     * Generates the content for reminder emails.
     *
     * @param events List of events for a user.
     * @return Email content as a string.
     */
    private String generateEmailContent(List<Event> events) {
        StringBuilder content = new StringBuilder();

        for (Event event : events) {
            content.append(
                    String.format("Title: %s\nDescription: %s\nStart: %s\nDuration: %s\n\n",
                            event.getTitle(),
                            event.getDescription(),
                            event.getStart(),
                            event.getDuration()
                    )
            ).append("\n\n");
        }

        return String.format("Hello!\n\nYou have the following events coming up:\n\n%s\n\nSincerely, your calendar app", content.toString());
    }
}
