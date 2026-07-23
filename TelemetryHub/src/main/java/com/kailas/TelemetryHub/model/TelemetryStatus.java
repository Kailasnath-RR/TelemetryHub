package com.kailas.TelemetryHub.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TelemetryStatus(String State,

                              @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                              LocalDateTime receivedAt) {
}

//TYPE=STATUS,STATE=XXXX