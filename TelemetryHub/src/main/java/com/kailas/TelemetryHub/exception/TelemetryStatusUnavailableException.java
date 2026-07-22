package com.kailas.TelemetryHub.exception;

public class TelemetryStatusUnavailableException extends RuntimeException{
    public TelemetryStatusUnavailableException(){
        super("Status unavailable.");
    }
}
