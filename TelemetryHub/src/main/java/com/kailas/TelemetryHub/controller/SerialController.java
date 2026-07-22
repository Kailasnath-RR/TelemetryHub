package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.SerialStatus;
import com.kailas.TelemetryHub.serial.SerialService;
import com.kailas.TelemetryHub.service.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serial")
public class SerialController {
    private final SerialService serialService;
    private final MachineService machineService;

    public SerialController(SerialService serialService,MachineService machineService){
        this.machineService = machineService;
        this.serialService = serialService;
    }

    @PostMapping("/disconnect") //disconnects com port
    public ResponseEntity<Void> disconnect(){

        serialService.disconnect();
        machineService.resetMachineState();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reconnect")
    public ResponseEntity<Void> reconnect(){
        serialService.reconnect();
        machineService.resetMachineState();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status")
    public ResponseEntity<SerialStatus> status(){
        SerialStatus s = serialService.status();
        return ResponseEntity.ok(s);
    }




}
