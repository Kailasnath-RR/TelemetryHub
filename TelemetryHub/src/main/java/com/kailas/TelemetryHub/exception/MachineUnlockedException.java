package com.kailas.TelemetryHub.exception;

public class MachineUnlockedException extends RuntimeException{
    public MachineUnlockedException(){
        super("Machine already Unlocked.");
    }
}
