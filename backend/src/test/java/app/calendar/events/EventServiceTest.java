package app.calendar.events;

import app.calendar.event.application.EventService;
import app.calendar.event.domain.Event;
import app.calendar.event.presentation.response.EventBlob;
import app.calendar.event.repository.EventRepository;
import app.calendar.user.domain.User;
import com.github.dockerjava.api.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        event1 = Event.builder()
                .id(1L)
                .title("Event 1")
                .description("Description 1")
                .start(LocalDateTime.now().plusMinutes(10))
                .duration(Duration.ofMinutes(30))
                .periodic(false)
                .build();

        event2 = Event.builder()
                .id(2L)
                .title("Event 2")
                .description("Description 2")
                .start(LocalDateTime.now().plusHours(2))
                .duration(Duration.ofMinutes(45))
                .periodic(true)
                .frequency(Duration.ofHours(1))
                .build();
    }

    @Test
    public void testGetEventsInInterval_withNonPeriodicEvents() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        List<EventBlob> eventBlobs = eventService.getEventsInInterval(start, end);

        verify(eventRepository, times(1)).findAll();

        assertNotNull(eventBlobs);
        assertEquals(1, eventBlobs.size()); // Only event1 should be returned in the interval
        assertEquals("Event 1", eventBlobs.get(0).getTitle());
    }

    @Test
    public void testGetEventsInInterval_withPeriodicEvents() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(3);

        when(eventRepository.findAll()).thenReturn(Arrays.asList(event1, event2));

        List<EventBlob> eventBlobs = eventService.getEventsInInterval(start, end);

        verify(eventRepository, times(1)).findAll();

        assertNotNull(eventBlobs);
        assertEquals(3, eventBlobs.size()); // Should include 2 periodic occurrences of event2 and event1
    }

    @Test
    public void testGetEventById_success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));

        Optional<Event> event = eventService.getEventById(1L);

        verify(eventRepository, times(1)).findById(1L);

        assertTrue(event.isPresent());
        assertEquals("Event 1", event.get().getTitle());
    }

    @Test
    public void testGetEventById_notFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Event> event = eventService.getEventById(99L);

        verify(eventRepository, times(1)).findById(99L);

        assertFalse(event.isPresent());
    }

    @Test
    public void testCreateEvent() {
        when(eventRepository.save(event1)).thenReturn(event1);

        Event createdEvent = eventService.createEvent(event1);

        verify(eventRepository, times(1)).save(event1);

        assertNotNull(createdEvent);
        assertEquals("Event 1", createdEvent.getTitle());
    }

    @Test
    public void testUpdateEvent_success() {
        Event updatedEvent = Event.builder()
                .id(1L)
                .title("Updated Event")
                .description("Updated Description")
                .start(LocalDateTime.now().plusHours(1))
                .duration(Duration.ofMinutes(60))
                .periodic(false)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event1));
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);

        Optional<Event> event = eventService.updateEvent(1L, updatedEvent);

        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(updatedEvent);

        assertTrue(event.isPresent());
        assertEquals("Updated Event", event.get().getTitle());
    }

    @Test
    public void testDeleteEvent_success() {
        when(eventRepository.existsById(1L)).thenReturn(true);

        boolean isDeleted = eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).existsById(1L);
        verify(eventRepository, times(1)).deleteById(1L);

        assertTrue(isDeleted);
    }

    @Test
    public void testDeleteEvent_notFound() {
        when(eventRepository.existsById(99L)).thenReturn(false);

        boolean isDeleted = eventService.deleteEvent(99L);

        verify(eventRepository, times(1)).existsById(99L);

        assertFalse(isDeleted);
    }

}
