package com.kailas.TelemetryHub.model;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard error response")
public record ErrorResponse(

        @Schema(
                description = "Time when the error occurred",
                example = "2026-07-28T12:34:56Z"
        )
        Instant timestamp,

        @Schema(
                description = "HTTP status code",
                example = "409"
        )
        int status,

        @Schema(
                description = "HTTP status name",
                example = "Conflict"
        )
        String error_type,

        @Schema(
                description = "Detailed error message",
                example = "Machine is already running"
        )
        String message
) {}