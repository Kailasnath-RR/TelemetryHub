package com.kailas.TelemetryHub.serial;

import org.springframework.stereotype.Component;

import  java.util.concurrent.ThreadLocalRandom;

@Component
public class PacketGenerator {
    private final String unlockedStatus = "TYPE=STATUS,STATE=UNLOCKED";
    private final String lockedStatus =  "TYPE=STATUS,STATE=LOCKED";
    private final String runningStatus =  "TYPE=STATUS,STATE=RUNNING";
    private final String pausedStatus =  "TYPE=STATUS,STATE=PAUSED";
    private  int count = 0;
    public String generateStatus(String status){
        switch (status){
            case "unlock":
                return unlockedStatus;

            case "lock" :
                return lockedStatus;

            case "run":
                return runningStatus;

            case "pause":
                return pausedStatus;

            default: return "INVALID_COMMAND";
        }
    }



    public String generatePacket(){
        int adc = ThreadLocalRandom.current().nextInt(0,1024);
        int sample_period = 10000;
        String line ="TYPE=DATA,COUNT="+count+",ADC="+adc+",SAMPLE_PERIOD="+sample_period;
        count++;
        return line;
    }
}
