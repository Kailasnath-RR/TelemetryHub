package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.mapper.TelemetryMapper;
import com.kailas.TelemetryHub.model.PageResponse;
import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.parser.SerialParser;
import com.kailas.TelemetryHub.repository.TelemetryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelemetryService {

    private TelemetryStatus latestStatus;  //holds the current status of the machine
    private TelemetryData latestData;

    private final SerialParser serialParser;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final TelemetryRepository telemetryRepository;
    private final TelemetryMapper telemetryMapper;

    public TelemetryService(SerialParser serialParser,SimpMessagingTemplate simpMessagingTemplate,TelemetryRepository telemetryRepository,TelemetryMapper telemetryMapper){
        this.telemetryMapper = telemetryMapper;
        this.telemetryRepository = telemetryRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.serialParser = serialParser;
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

    public PageResponse<TelemetryData> getHistory(Integer adcMin,Integer adcMax,Pageable pageable){

        if(adcMin == null && adcMax == null)
        {
        Page<TelemetryData> pageData =
                telemetryRepository.findAll(pageable)
                .map(telemetryMapper::toTelemetryDto);

        return telemetryMapper.toPageResponse(pageData);
        }else if(adcMax == null){
            Page<TelemetryData> pageData = telemetryRepository
                    .findByadcValueGreaterThan(adcMin,pageable)
                    .map(telemetryMapper::toTelemetryDto);

            return telemetryMapper.toPageResponse(pageData);
        }
        else if(adcMin != null && adcMax !=null){
            Page<TelemetryData> pageData = telemetryRepository
                    .findByadcValueBetween(adcMin,adcMax,pageable)
                    .map(telemetryMapper::toTelemetryDto);
            return telemetryMapper.toPageResponse(pageData);
        }
        return null;
    }

    public List<Double> getVoltage(){
        List<TelemetryEntity> data = telemetryRepository.findAll();
        List<Double> voltageData = new ArrayList<>();
        for(TelemetryEntity entity:data){
            voltageData.add((entity.getAdcValue()*3.3)/1023);
        }
        return voltageData;
    }

}
