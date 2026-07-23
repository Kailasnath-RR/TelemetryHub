package com.kailas.TelemetryHub.exception;

public class MachineTooFastException extends RuntimeException{
    public MachineTooFastException(){
        super("Machine speed too fast. Looping back to initial speed.");
    }
}
