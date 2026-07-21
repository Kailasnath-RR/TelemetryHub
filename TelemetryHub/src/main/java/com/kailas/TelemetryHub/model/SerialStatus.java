package com.kailas.TelemetryHub.model;

public record SerialStatus(
        boolean connected,
        String portName) {
}
