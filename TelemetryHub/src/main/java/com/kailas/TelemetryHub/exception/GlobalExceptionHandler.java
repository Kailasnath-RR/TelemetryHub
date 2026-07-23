package com.kailas.TelemetryHub.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MachineAlreadyRunningException.class)
    public ResponseEntity<String> handleMachineAlreadyRunning(MachineAlreadyRunningException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());

    }

    @ExceptionHandler(MachineLockedException.class)
    public ResponseEntity<String> handleMachineLocked(MachineLockedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(SerialCommunicationException.class)
    public ResponseEntity<String> handleSerialCommunication(SerialCommunicationException ex){
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(MachineAlreadyStoppedException.class)
    public ResponseEntity<String> handleMachineStopped(MachineAlreadyStoppedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MachineUnlockedException.class)
    public ResponseEntity<String> handleMachineUnlocked(MachineUnlockedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(SerialPortDisconnectedException.class)
    public ResponseEntity<String> handleSerialPortDisconnect(SerialPortDisconnectedException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(TelemetryStatusUnavailableException.class)
    public ResponseEntity<String> handleTelemetryStatus(TelemetryStatusUnavailableException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MachineTooFastException.class)
    public ResponseEntity<String> handleMachineTooFast(MachineTooFastException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
