package com.kailas.TelemetryHub.model;


import java.time.LocalDateTime;

public record TelemetryStatsFilter(
        LocalDateTime from,
        LocalDateTime to,
        Integer minAdc,
        Integer maxAdc

) {
}
