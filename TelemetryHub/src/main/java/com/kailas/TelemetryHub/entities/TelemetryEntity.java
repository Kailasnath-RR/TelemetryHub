package com.kailas.TelemetryHub.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer count;
    private  Integer adcValue;
    private Integer samplePeriod;
    private LocalDateTime receivedAt;
}
