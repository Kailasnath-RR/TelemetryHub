package com.kailas.TelemetryHub.serial;


import com.fazecast.jSerialComm.SerialPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Scanner;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;


@Component
public class SerialService implements CommandLineRunner {
    SerialPort comPort = SerialPort.getCommPort("COM19");

    private BufferedWriter writeData;
    private BufferedReader readData;

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

    public synchronized void sendData(String text) throws  Exception{
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



    public synchronized void readUART(){
        try{
            if (readData != null) {
                line = readData.readLine();
                if (line != null && !line.trim().isEmpty()) {
                    System.out.println("Recieved: " + line);
                }
            }
        }catch(IOException e){
            if(isRunning && !e.getMessage().contains("timed out")){
                System.err.println("Actual UART error: "+e.getMessage());
            }
        }
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

    private void startConsoleListener() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("=== Telemetry Active. Type 'quit' or 'exit' anytime to stop. ===");

            while (isRunning) {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if ("quit".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)) {
                        shutdownHardware();
                        // Exit application cleanly
                        System.exit(0);
                    } else if ("stop".equalsIgnoreCase(input)) {
                        try { stopMachine(); } catch (Exception e) { System.err.println(e.getMessage()); }
                    } else if ("start".equalsIgnoreCase(input)) {
                        try { startMachine(); } catch (Exception e) { System.err.println(e.getMessage()); }
                    }else if ("unlock".equalsIgnoreCase(input)){
                        try { unlockMachine();} catch (Exception e) { System.err.println(e.getMessage()); }
                    }
                }
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    @Override
    public void run(String... args) throws Exception {
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

            startConsoleListener();

            while(comPort.isOpen() && isRunning){
                try{
                    if(readData.ready()){
                        readUART();
                    }
                }
                catch (Exception e){
                    System.out.println(e);
                }


            }







        }
    }
}
