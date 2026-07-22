package com.kailas.TelemetryHub.exception;

public class SerialPortDisconnectedException extends RuntimeException {

    public SerialPortDisconnectedException(){
        super("Serial UART Port disconnected.");
    }

}
