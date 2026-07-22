package com.kailas.TelemetryHub.exception;

public class MachineAlreadyStoppedException extends RuntimeException{
    public MachineAlreadyStoppedException(){
        super("Machine already stopped");
    }
}
