package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.entities.TelemetryAlert;
import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.mapper.TelemetryMapper;
import com.kailas.TelemetryHub.model.*;
import com.kailas.TelemetryHub.parser.SerialParser;
import com.kailas.TelemetryHub.repository.AlertRepository;
import com.kailas.TelemetryHub.repository.TelemetryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;


@Service
public class TelemetryService {
    @Value("${alert.Adc-threshold}")
    private int adcThreshold;
    private TelemetryStatus latestStatus;  //holds the current status of the machine
    private TelemetryData latestData;

    private final SerialParser serialParser;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TelemetryRepository telemetryRepository;
    private final TelemetryMapper telemetryMapper;
    private final AlertRepository alertRepository;
    public TelemetryService(SerialParser serialParser,
                            SimpMessagingTemplate simpMessagingTemplate,
                            TelemetryRepository telemetryRepository,
                            TelemetryMapper telemetryMapper,
                            AlertRepository alertRepository){

        this.telemetryMapper = telemetryMapper;
        this.telemetryRepository = telemetryRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.serialParser = serialParser;
        this.alertRepository = alertRepository;
    }
    private void checkForAlerts(TelemetryEntity entity){
        if(entity.getAdcValue() > adcThreshold){
            TelemetryAlert telemetryAlert= TelemetryAlert.builder().telemetry(entity).createdAt(LocalDateTime.now()).message("Adc value crossed threshold").build();
            alertRepository.save(telemetryAlert);
        }
    }

    public void parse(String Line){
            String[] actionParsed = serialParser.getActionType(Line);

            if(actionParsed == null)return;

            String Action = actionParsed[0];
            String data = actionParsed[1];



            if(Action.equals("STATUS")){
                TelemetryStatus parsedStatus = serialParser.parseStatus(data);

                if (parsedStatus != null) {
                    latestStatus = parsedStatus;
                    simpMessagingTemplate.convertAndSend("/topic/status",latestStatus);
                }

            }
            if(Action.equals("DATA")){
                TelemetryData parsedData = serialParser.parseData(data);

                if (parsedData != null) {
                    latestData = parsedData;
                    TelemetryEntity entity = telemetryMapper.toTelemetryEntity(latestData);
                    telemetryRepository.save(entity);
                    checkForAlerts(entity);
                    simpMessagingTemplate.convertAndSend("/topic/telemetry",latestData);

                }
            }
    }
    public TelemetryData getLatestData(){
        if(latestData == null)  return null;
        return latestData;
    }

    public TelemetryStatus getLatestStatus(){
        if(latestStatus == null) return null;
        return latestStatus;
    }

    public PageResponse<TelemetryData> getHistory(TelemetryHistoryFilter filter,Pageable pageable){

        if(filter.adcMin() == null && filter.adcMax() == null)
        {
        Page<TelemetryData> pageData =
                telemetryRepository.findAll(pageable)
                .map(telemetryMapper::toTelemetryDto);

        return telemetryMapper.toPageResponse(pageData);
        }else if(filter.adcMin()!=null && filter.adcMax() == null){
            Page<TelemetryData> pageData = telemetryRepository
                    .findByadcValueGreaterThan(filter.adcMin(),pageable)
                    .map(telemetryMapper::toTelemetryDto);

            return telemetryMapper.toPageResponse(pageData);
        }
        else if(filter.adcMin() == null ){
            Page<TelemetryData> pageData = telemetryRepository
                    .findByadcValueLessThan(filter.adcMax(),pageable)
                    .map(telemetryMapper::toTelemetryDto);
            return telemetryMapper.toPageResponse(pageData);
        }
        else {
            Page<TelemetryData> pageData = telemetryRepository
                    .findByadcValueBetween(filter.adcMin(),filter.adcMax(),pageable)
                    .map(telemetryMapper::toTelemetryDto);
            return telemetryMapper.toPageResponse(pageData);
        }
    }

    /*public List<Double> getVoltage(){
        List<TelemetryEntity> data = telemetryRepository.findAll();
        List<Double> voltageData = new ArrayList<>();
        for(TelemetryEntity entity:data){
            voltageData.add((entity.getAdcValue()*3.3)/1023);
        }
        return voltageData;
    }*/   //quick method for testing voltage readings

    public TelemetryStat getStat(TelemetryStatsFilter filter){
        return telemetryRepository.findStats(filter.from(),filter.to(),filter.minAdc(),filter.maxAdc());
    }

}
