package com.kailas.TelemetryHub.serial;

import com.kailas.TelemetryHub.exception.SerialCommunicationException;
import com.kailas.TelemetryHub.service.MachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fazecast.jSerialComm.SerialPort;
import com.kailas.TelemetryHub.model.SerialStatus;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(SerialService.class);

    private Thread readerThread = null;
    private BufferedWriter writeData;
    private BufferedReader readData;

    private final TelemetryService telemetryService;

    private boolean isConnected = FALSE;
    private volatile boolean isRunning = TRUE;

    @Value("${serial.write-timeout}")
    private int writeTimeout ;

    @Value("${serial.read-timeout}")
    private int readTimeout ; //assumes data flows in every 3 seconds

    @Value("${serial.baud-rate}")
    private int baudRate ;

    @Value("${serial.stop-bit}")
    private int stopBit ;

    @Value("${serial.data-bit}")
    private int dataBit;

    private final String password = "ABC"; //hardcoded string in firmware
    private final String startMachineCode = "s"; //hardcoded chars in firmware
    private final String stopMachineCode = "p";
    private final String lockMachineCode = "L";

    public SerialService(TelemetryService telemetryService){
        this.telemetryService = telemetryService;
    }

    public void unlockMachine() {
        sendData(password);
    }

    public void startMachine() {
        sendData(startMachineCode);
    }

    public void stopMachine() {
        sendData(stopMachineCode);
    }

    public void lockMachine() {
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
            logger.info("Connected to {}",comPort.getSystemPortName());
            isRunning = TRUE;
            isConnected = TRUE;
            OutputStream out = comPort.getOutputStream();
            writeData = new BufferedWriter(new OutputStreamWriter(out));
            InputStream in = comPort.getInputStream();
            readData = new BufferedReader(new InputStreamReader(in));

    }
    public void disconnect(){
        shutdownHardware();

        try{

            Thread.sleep(500);
            isRunning = FALSE;
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
                logger.info("Disconnected from {}",comPort.getSystemPortName());
            }
        } catch (Exception e) {
            logger.error("Disconnect failed: ",e);
        }

    }

    public void reconnect(){
        disconnect();
        connect();
        startReader();
    }

    private synchronized void sendData(String text) {
        try{
            writeData.write(text);
            writeData.flush();
        } catch (IOException e) {
            logger.error("Failed to send UART communication ",e);
            throw new SerialCommunicationException(e);
        }
    }



    public SerialStatus status(){
        boolean Lconnected = isConnected;
        String LportName;
        if(comPort!=null){
            LportName = comPort.getSystemPortName();
        }else{
            LportName = "none";
        }

        return new SerialStatus(Lconnected,LportName);
    }

    private synchronized String readUART(){
        try{
            if (readData != null) {

                return readData.readLine();
            }
        }catch(IOException e){
            if(isRunning && !e.getMessage().contains("timed out")){
                logger.error("Actual UART error: ",e);
            }
        }
        return null;
    }

    public synchronized void shutdownHardware() {
        if (!isRunning) return; // Prevent duplicate cleanup execution
        logger.info("Initiating graceful shutdown sequence...");

        try {
            if (writeData != null) {
                logger.info("Sending STOP command ('p')...");
                sendData(stopMachineCode);
                Thread.sleep(300); // MCU needs execution time to pause telemetry

                logger.info("Sending LOCK command ('L')...");
                sendData(lockMachineCode);
                Thread.sleep(300);
            }

        } catch (Exception e) {
                logger.error("Teardown error: ",e);
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
                    logger.error("Reader Thread Failed: ",e);
                }
            }
            logger.info("Reader Thread Stopped.");
        },"UART Reader Thread");
        readerThread.start();
        logger.info("Reader Thread started");
    }

    @EventListener(ContextClosedEvent.class)
    public void gracefulForcedShutdown(){

        disconnect();
    }

    @Override
    public void run(String... args){
        connect();
        startReader();

    }

}


