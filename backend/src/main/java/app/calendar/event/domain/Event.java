package app.calendar.event.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="_event")
public class Event {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String description;

    private LocalDateTime start;
    private Duration duration;

    private boolean periodic;
    private Duration frequency;
}
