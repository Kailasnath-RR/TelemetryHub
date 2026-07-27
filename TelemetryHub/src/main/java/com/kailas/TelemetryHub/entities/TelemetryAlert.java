package com.kailas.TelemetryHub.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TelemetryAlert")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "telemetry_id")
    private TelemetryEntity telemetry;

    private LocalDateTime createdAt;
    private String message;
}
