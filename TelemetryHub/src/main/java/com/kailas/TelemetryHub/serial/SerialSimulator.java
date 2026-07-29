package com.kailas.TelemetryHub.serial;


import com.kailas.TelemetryHub.model.SerialStatus;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.BooleanUtils.FALSE;
import static org.apache.commons.lang3.BooleanUtils.TRUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Profile("sim")
public class SerialSimulator implements CommandLineRunner,SerialInterface {

    private final TelemetryService telemetryService;
    private final PacketGenerator packetGenerator;
    Logger logger = LoggerFactory.getLogger(SerialSimulator.class);
    private boolean isConnected = false;
    private boolean isRunning = false;
    private boolean isLocked = false;

    public SerialSimulator(TelemetryService telemetryService,PacketGenerator packetGenerator){
        this.telemetryService = telemetryService;
        this.packetGenerator = packetGenerator;
    }

    public void startReader(){

        Thread simulatorThread = new Thread(()->{
            while(true){
                if(isConnected && isRunning){
                    telemetryService.parse(packetGenerator.generatePacket());
                }
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    Thread.currentThread().interrupt();
                    break;
                }


            }
        });
        simulatorThread.setDaemon(true);
        simulatorThread.start();
    }
    @Override
    public void run(String... args) throws Exception {
        connect();
        startReader();

    }

    @Override
    public void connect() {
        if(!isConnected){
            isConnected = true;
            logger.info("Connected to serial port");

        }

    }

    @Override
    public void disconnect() {
        if(isConnected){
            isConnected = false;
            logger.info("Disconnected from serial port.");

        }
    }

    @Override
    public void reconnect() {
        disconnect();
        connect();
    }

    @Override
    public SerialStatus status() {
        return new SerialStatus(isConnected,"COM18");
    }

    @Override
    public void startMachine() {
        if(!isRunning){
            isRunning = true;
            telemetryService.parse(packetGenerator.generateStatus("run"));
            logger.info("Machine running");
        }
    }

    @Override
    public void stopMachine() {
        if(isRunning){
            isRunning = false;
            telemetryService.parse(packetGenerator.generateStatus("pause"));
            logger.info("Machine stopped.");
        }
    }

    @Override
    public void lockMachine() {
        if(!isLocked){
            isLocked = true;
            telemetryService.parse(packetGenerator.generateStatus("lock"));
            logger.info("Machine Locked");
        }
    }

    @Override
    public void unlockMachine() {
        if(isLocked){
            isLocked = false;
            telemetryService.parse(packetGenerator.generateStatus("unlock"));
            logger.info("Machine unlocked");
        }
    }

    @Override
    public void machineSpeedIncrease() {
        logger.info("Machine speed increased.");
        return;
    }

    @Override
    public void machineSpeedDecrease() {
        logger.info("Machine speed decreased.");
        return;
    }

    @Override
    public void shutdownHardware() {
        stopMachine();
        lockMachine();
    }

}
