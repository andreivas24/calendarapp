package app.calendar.mail;

import app.calendar.mailing.application.MailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MailService.
 */
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailService mailService;

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests successful sending of a plain text email.
     */
    @Test
    public void testSendReminder_success() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Email Content";

        mailService.sendReminder(to, subject, text);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests sending a plain text email with an invalid email address.
     */
    @Test
    public void testSendReminder_withInvalidEmail() {
        doThrow(new RuntimeException("Invalid email address")).when(mailSender).send(any(SimpleMailMessage.class));

        try {
            mailService.sendReminder("invalid-email", "Test Subject", "Test Content");
        } catch (RuntimeException e) {
            assertEquals("Invalid email address", e.getMessage());
        }

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }


}
