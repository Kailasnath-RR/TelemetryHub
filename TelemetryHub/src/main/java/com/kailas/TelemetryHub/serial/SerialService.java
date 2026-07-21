package com.kailas.TelemetryHub.serial;


import com.fazecast.jSerialComm.SerialPort;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;


@Component
public class SerialService implements CommandLineRunner {

    SerialPort[] portNames = SerialPort.getCommPorts();
    SerialPort comPort = null;


    private BufferedWriter writeData;
    private BufferedReader readData;
    private final TelemetryService telemetryService;
    private String line;

    private volatile boolean isRunning = TRUE;

    private int writeTimeout = 1000;
    private int readTimeout = 3000;  //assuming data flows in every 3s
    private int baudRate = 9600;
    private int stopBit = 1;
    private int dataBit = 8;

    private String password = "ABC"; //hardcoded string in firmware
    private String startMachineCode = "s"; //hardcoded chars in firmware
    private String stopMachineCode = "p";
    private String lockMachineCode = "L";

    public SerialService(TelemetryService telemetryService){
        this.telemetryService = telemetryService;
    }

    private synchronized void sendData(String text) throws  Exception{
        writeData.write(text);
        writeData.flush();
    }

    public void unlockMachine() throws Exception{
            sendData(password);
    }

    public void startMachine() throws Exception{
        sendData(startMachineCode);
    }

    public void stopMachine() throws Exception{
        sendData(stopMachineCode);
    }

    public void lockMachine() throws Exception{
        sendData(lockMachineCode);
    }



    private synchronized String readUART(){
        try{
            if (readData != null) {
                return readData.readLine();
            }
        }catch(IOException e){
            if(isRunning && !e.getMessage().contains("timed out")){
                System.err.println("Actual UART error: "+e.getMessage());
            }
        }
        return null;
    }

    public synchronized void shutdownHardware() {
        if (!isRunning) return; // Prevent duplicate cleanup execution

        System.out.println("\nInitiating graceful shutdown sequence...");
        this.isRunning = false;

        try {
            if (writeData != null) {
                System.out.println("Sending STOP command ('p')...");
                sendData(stopMachineCode);
                Thread.sleep(300); // MCU needs execution time to pause telemetry

                System.out.println("Sending LOCK command ('L')...");
                sendData(lockMachineCode);
                Thread.sleep(300);

                writeData.close();
            }
            if (readData != null) {
                readData.close();
            }
        } catch (Exception e) {
            System.err.println("Teardown error: " + e.getMessage());
        } finally {
            if (comPort != null && comPort.isOpen()) {
                comPort.closePort();
                System.out.println("ComPort closed successfully.");
            }
        }
    }

    @EventListener(ContextClosedEvent.class)
    public void gracefulForcedShutdown(){
        shutdownHardware();
    }


    @Override
    public void run(String... args) throws Exception {

        if(telemetryService == null) throw new IllegalStateException("Telemetry Service not initialized");

        ;
        for(SerialPort port:portNames){
            if(port.getPortDescription().contains("UART")){
                comPort = port;
                break;
            }

        }
        if(comPort == null) throw new IllegalStateException("UART COM PORT NOT FOUND");
        comPort.setBaudRate(baudRate);
        comPort.setNumDataBits(dataBit);
        comPort.setNumStopBits(stopBit);
        comPort.setParity(SerialPort.NO_PARITY);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING,readTimeout,writeTimeout);




        if(comPort.openPort()){

            OutputStream out = comPort.getOutputStream();
            writeData = new BufferedWriter(new OutputStreamWriter(out));
            InputStream in = comPort.getInputStream();
            readData = new BufferedReader(new InputStreamReader(in));

            while(comPort.isOpen() && isRunning){
                try{
                    if(readData.ready()){
                        line = readUART();
                        telemetryService.parse(line);

                    }
                }
                catch (Exception e){
                    System.out.println(e);
                }


            }







        }
    }
}
