package app.calendar.mailing.shedule;

import app.calendar.mailing.application.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventReminder {
    @Autowired
    private MailService mailService;

    @Scheduled(fixedRate = 3600000) // Every hour
    public void sendEventReminders() {
        System.out.println("Checking for close events.");
    }

    private String generateEmailContent() {
        return "";
    }
}
