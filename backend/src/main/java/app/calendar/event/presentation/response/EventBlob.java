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
public class EventBlob {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime start;
    private Duration duration;

    public static EventBlob mapFromEvent(Event event) {
        return EventBlob.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .start(event.getStart())
                .duration(event.getDuration())
                .build();
    }
}
