package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.service.MachineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machine")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService){
        this.machineService = machineService;

    }

    @PostMapping("/start")
    public ResponseEntity<Void> startMachine(){
            machineService.startMachine();
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/stop")
    public ResponseEntity<Void> stopMachine(){
            machineService.stopMachine();
            return ResponseEntity.noContent().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<Void> unlockMachine(){
            machineService.unlockMachine();
            return ResponseEntity.noContent().build();

        }

    @PostMapping("/lock")
    public ResponseEntity<Void> lockMachine(){

        machineService.lockMachine();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shutdownHardware")
    public ResponseEntity<Void> shutdownHardware(){

        machineService.shutdownMachine();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/speed-increase")
    public ResponseEntity<Void> speedInc(){
        machineService.machineSpeedIncrease();
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/speed-decrease")
    public ResponseEntity<Void> speedDec(){
        machineService.machineSpeedDecrease();
        return ResponseEntity.noContent().build();

    }
}
