package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService ;

    public TelemetryController(TelemetryService telemetryService){
        this.telemetryService = telemetryService;
    }

    @GetMapping("/latest/data")
    public TelemetryData getLatestData(){
       return telemetryService.getLatestData();
    }


    @GetMapping("/latest/status")
    public TelemetryStatus getLatestStatus(){
        return telemetryService.getLatestStatus();
    }

    @GetMapping("/history")
    public Map<Integer, TelemetryData> getHistory(){
        return telemetryService.getHistory();
    }




}
