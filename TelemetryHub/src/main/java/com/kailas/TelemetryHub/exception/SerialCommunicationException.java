package com.kailas.TelemetryHub.exception;

public class SerialCommunicationException extends RuntimeException {
    public SerialCommunicationException(Throwable cause){
        super("Failed to communicate with hardware",cause);
    }
}
