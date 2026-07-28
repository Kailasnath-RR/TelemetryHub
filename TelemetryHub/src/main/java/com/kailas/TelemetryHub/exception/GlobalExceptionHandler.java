package com.kailas.TelemetryHub.exception;


import com.kailas.TelemetryHub.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MachineAlreadyRunningException.class)
    public ResponseEntity<ErrorResponse> handleMachineAlreadyRunning(MachineAlreadyRunningException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

    }

    @ExceptionHandler(MachineLockedException.class)
    public ResponseEntity<ErrorResponse> handleMachineLocked(MachineLockedException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"Conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SerialCommunicationException.class)
    public ResponseEntity<ErrorResponse> handleSerialCommunication(SerialCommunicationException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),503,"Service Unavailable",ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(MachineAlreadyStoppedException.class)
    public ResponseEntity<ErrorResponse> handleMachineStopped(MachineAlreadyStoppedException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"Conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MachineUnlockedException.class)
    public ResponseEntity<ErrorResponse> handleMachineUnlocked(MachineUnlockedException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(SerialPortDisconnectedException.class)
    public ResponseEntity<ErrorResponse> handleSerialPortDisconnect(SerialPortDisconnectedException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TelemetryStatusUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleTelemetryStatus(TelemetryStatusUnavailableException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MachineTooFastException.class)
    public ResponseEntity<ErrorResponse> handleMachineTooFast(MachineTooFastException ex){
        ErrorResponse error = new ErrorResponse(Instant.now(),409,"conflict",ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

}
