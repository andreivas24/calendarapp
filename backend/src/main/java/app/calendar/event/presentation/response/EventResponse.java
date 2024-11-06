package app.calendar.event.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {
    private String title;
    private String description;
    private LocalDateTime start;
    private Duration duration;
}
