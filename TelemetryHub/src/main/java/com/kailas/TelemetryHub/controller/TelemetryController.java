package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.PageResponse;
import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    public ResponseEntity<PageResponse<TelemetryData>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer adcMin,
            @RequestParam(required = false) Integer adcMax)
    {

        Pageable pageable = PageRequest.of(page,size);

        return  ResponseEntity.ok(telemetryService.getHistory(adcMin,adcMax,pageable));
    }

}
