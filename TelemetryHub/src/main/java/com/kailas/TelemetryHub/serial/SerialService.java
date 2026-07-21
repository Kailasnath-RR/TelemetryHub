package com.kailas.TelemetryHub.serial;


import com.fazecast.jSerialComm.SerialPort;
import com.kailas.TelemetryHub.model.SerialStatus;
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

    SerialPort comPort = null;

    private Thread readerThread = null;
    private BufferedWriter writeData;
    private BufferedReader readData;
    private final TelemetryService telemetryService;
    private boolean isConnected = FALSE;

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

    public void connect(){
        SerialPort[] portNames = SerialPort.getCommPorts();
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
        comPort.openPort();
        if (!comPort.openPort()) {
            throw new IllegalStateException("Failed to open COM port");
        }
            isRunning = TRUE;
            isConnected = TRUE;
            OutputStream out = comPort.getOutputStream();
            writeData = new BufferedWriter(new OutputStreamWriter(out));
            InputStream in = comPort.getInputStream();
            readData = new BufferedReader(new InputStreamReader(in));

    }
    public void disconnect(){
        shutdownHardware();
        isRunning = FALSE;

        try{
            if(readerThread != null){
                readerThread.join(1000);  //waits for the startReader thread to exit safely

                if(!readerThread.isAlive()){
                    readerThread = null;
                }
            }
            if(writeData != null){
                writeData.close();
            }
            if(readData != null){
                readData.close();
            }
            if (comPort != null && comPort.isOpen()) {
                isConnected = FALSE;
                comPort.closePort();
                System.out.println("ComPort closed successfully.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void reconnect(){
        disconnect();
        connect();
        startReader();
    }

    private synchronized void sendData(String text) throws  Exception{
        writeData.write(text);
        writeData.flush();
    }



    public SerialStatus status(){
        boolean Lconnected = isConnected;
        String LportName = comPort.getSystemPortName();

        return new SerialStatus(Lconnected,LportName);
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


        try {
            if (writeData != null) {
                System.out.println("Sending STOP command ('p')...");
                sendData(stopMachineCode);
                Thread.sleep(300); // MCU needs execution time to pause telemetry

                System.out.println("Sending LOCK command ('L')...");
                sendData(lockMachineCode);
                Thread.sleep(300);
            }

        } catch (Exception e) {
            System.err.println("Teardown error: " + e.getMessage());
        }
    }

    public void startReader(){
        if(readerThread != null && readerThread.isAlive()) return;

        readerThread = new Thread(()->{
            while(comPort.isOpen() && isRunning){
                try{
                    if(readData.ready()){
                        String line = readUART();
                        telemetryService.parse(line);

                    }
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        readerThread.start();
    }
    @EventListener(ContextClosedEvent.class)
    public void gracefulForcedShutdown(){
        disconnect();
    }


    @Override
    public void run(String... args) throws Exception {
        connect();
        startReader();

    }

}


