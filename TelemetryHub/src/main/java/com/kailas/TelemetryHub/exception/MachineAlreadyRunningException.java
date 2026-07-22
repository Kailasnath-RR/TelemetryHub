package com.kailas.TelemetryHub.exception;

public class MachineAlreadyRunningException extends RuntimeException{

    public MachineAlreadyRunningException(){
        super("Machine already running.");
    }
}
