package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.SerialStatus;
import com.kailas.TelemetryHub.serial.SerialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/serial")
public class SerialController {
    private final SerialService serialService;

    public SerialController(SerialService serialService){
        this.serialService = serialService;
    }

    @PostMapping("/disconnect") //disconnects com port
    public void disconnect(){
        serialService.disconnect();
    }

    @PostMapping("/reconnect")
    public void reconnect(){
        serialService.reconnect();
    }

    @GetMapping("/status")
    public SerialStatus status(){
        return serialService.status();
    }




}
