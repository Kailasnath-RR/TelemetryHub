package com.kailas.TelemetryHub.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {

        super("Invalid refresh token.");
    }
}
