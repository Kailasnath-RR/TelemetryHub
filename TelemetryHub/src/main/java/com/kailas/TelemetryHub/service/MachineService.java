package com.kailas.TelemetryHub.service;


import com.kailas.TelemetryHub.exception.*;
import com.kailas.TelemetryHub.model.SerialStatus;
import com.kailas.TelemetryHub.serial.SerialService;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;



@Service
public class MachineService {

    private final SerialService serialService;
    private static final Logger logger = LoggerFactory.getLogger(MachineService.class);

    private int PR3 = 10000;
    private boolean isLocked = TRUE;
    private boolean isRunning = FALSE;

    public MachineService(SerialService serialService){

        this.serialService = serialService;
    }

    public void checkConnection(){
        SerialStatus ss = serialService.status();
        if(!ss.connected()){
            logger.warn("Serial port is disconnected. Unable to send UART commands");
            throw new SerialPortDisconnectedException();
        }
    }

    public void resetMachineState() {
        isRunning = FALSE;
        isLocked = TRUE;

    }

    public void startMachine(){
            checkConnection();
            if(isLocked){
                logger.warn("Cannot start machine because it is locked.");
                throw new MachineLockedException();
            }

            if(isRunning){
                logger.warn("Machine is already running.");
                throw new MachineAlreadyRunningException();
            }

            serialService.startMachine();
            isRunning = TRUE;
            logger.info("Machine started.");
    }


    public void stopMachine(){
        checkConnection();
        if(isLocked) {
                logger.warn("Cannot stop machine because it is locked.");
                throw new MachineLockedException();
        }
        if(!isRunning) {
                logger.warn("Machine is already stopped.");
                throw new MachineAlreadyStoppedException();
        }
        serialService.stopMachine();
        isRunning = FALSE;
        logger.info("Machine stopped.");

    }

    public void unlockMachine(){
        checkConnection();
        if(!isLocked){
            logger.warn("Machine already unlocked.");
            throw new MachineUnlockedException();
        }

            serialService.unlockMachine();
            isLocked =FALSE;
            logger.info("Machine Unlocked.");

    }

    public void lockMachine(){
        checkConnection();
        if(isLocked){
                logger.warn("Machine is already locked.");
                throw new MachineLockedException();
            }
            serialService.lockMachine();
            isLocked =TRUE;
            logger.info("Machine locked.");

    }

    public void shutdownMachine(){
        checkConnection();
        serialService.shutdownHardware();
        isRunning = FALSE;
        isLocked = TRUE;
    }

    public void machineSpeedIncrease(){
        if(PR3 < 4000){
            PR3 = 10000;
            logger.warn("Machine speed below 4000, looping back to 10000"); //syncing it to match the firmware implementation
            throw new MachineTooFastException();
        }

        serialService.machineSpeedIncrease();
        PR3 -= 2000;
        logger.info("Machine speed increased");
    }

    public void machineSpeedDecrease(){
        serialService.machineSpeedDecrease();
        PR3 += 2000;
        logger.info("Machine speed decreased");
    }



    }





