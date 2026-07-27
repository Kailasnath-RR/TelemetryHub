package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.*;
import com.kailas.TelemetryHub.service.TelemetryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            TelemetryHistoryFilter filter)
    {

        Pageable pageable = PageRequest.of(page,size);

        return  ResponseEntity.ok(telemetryService.getHistory(filter,pageable));
    }

    /*@GetMapping("/voltage")
    public ResponseEntity<List<Double>> getVoltage(){
            return ResponseEntity.ok(telemetryService.getVoltage());
    }*/

    @GetMapping("/stats")
    public ResponseEntity<TelemetryStat> getStats(@ModelAttribute TelemetryStatsFilter filter){
        return ResponseEntity.ok(telemetryService.getStat(filter));
    }
}
