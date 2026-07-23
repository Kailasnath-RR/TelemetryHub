package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService ;

    public TelemetryController(TelemetryService telemetryService){
        this.telemetryService = telemetryService;
    }

    @GetMapping("/latest/data")
    public ResponseEntity<TelemetryData> getLatestData(){
        TelemetryData t = telemetryService.getLatestData();
        return ResponseEntity.ok(t);
    }


    @GetMapping("/latest/status")
    public ResponseEntity<TelemetryStatus> getLatestStatus(){
        TelemetryStatus t = telemetryService.getLatestStatus();
        return  ResponseEntity.ok(t);
    }

    @GetMapping("/history")
    public ResponseEntity<List<TelemetryData>> getHistory(){

        List<TelemetryData> t = telemetryService.getHistory();
        return  ResponseEntity.ok(t);
    }




}
