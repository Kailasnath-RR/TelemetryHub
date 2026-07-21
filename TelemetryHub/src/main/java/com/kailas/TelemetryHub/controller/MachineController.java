package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.service.MachineService;
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
    public void startMachine(){
            machineService.startMachine();
    }

    @PostMapping("/stop")
    public void stopMachine(){
            machineService.stopMachine();

    }

    @PostMapping("/unlock")
    public void unlockMachine(){
            machineService.unlockMachine();
        }

    @PostMapping("/lock")
    public void lockMachine(){
            machineService.lockMachine();
    }

    @PostMapping("/shutdownHardware")
    public void shutdownHardware(){
            machineService.shutdownMachine();
    }

}
