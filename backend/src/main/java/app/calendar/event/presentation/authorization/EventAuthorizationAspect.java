package app.calendar.event.presentation.authorization;

import app.calendar.event.domain.Event;
import app.calendar.event.presentation.exception.BadRequestException;
import app.calendar.event.repository.EventRepository;
import app.calendar.user.domain.User;
import app.calendar.user.repository.UserRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EventAuthorizationAspect {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Pointcut("@annotation(UserOwnsEvent)")
    public void userOwnsEventMethods() {}

    @Before("userOwnsEventMethods() && args(eventId,..)")
    public void checkUserOwnsEvent(Long eventId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You do not have permission to access this event");
        }
    }
}
