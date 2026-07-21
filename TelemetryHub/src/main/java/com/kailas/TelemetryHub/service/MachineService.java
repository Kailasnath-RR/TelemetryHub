package com.kailas.TelemetryHub.service;


import com.kailas.TelemetryHub.serial.SerialService;
import org.springframework.stereotype.Service;



@Service
public class MachineService {

    private final SerialService serialService;

    public MachineService(SerialService serialService){
        this.serialService = serialService;
    }

    public void startMachine(){
        try {
            serialService.startMachine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void stopMachine(){
        try {
            serialService.stopMachine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void unlockMachine(){
        try {
            serialService.unlockMachine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void lockMachine(){
        try {
            serialService.lockMachine();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void shutdownMachine(){
        serialService.shutdownHardware();
    }


    }





