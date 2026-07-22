package com.kailas.TelemetryHub.exception;

public class MachineLockedException extends RuntimeException{
    public MachineLockedException(){
        super("Machine is currently locked");
    }
}
