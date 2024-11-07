package app.calendar.event.presentation.response;

import app.calendar.event.domain.Event;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventInfo {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime start;
    private Duration duration;
    private boolean periodic;
    private Duration frequency;

    public static EventInfo mapFromEvent(Event event) {
        return EventInfo.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .start(event.getStart())
                .duration(event.getDuration())
                .periodic(event.isPeriodic())
                .frequency(event.getFrequency())
                .build();
    }
}
