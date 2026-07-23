package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.mapper.TelemetryMapper;
import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.parser.SerialParser;

import com.kailas.TelemetryHub.repository.TelemetryRepository;
import org.springframework.stereotype.Service;


import java.util.List;


import org.springframework.messaging.simp.SimpMessagingTemplate;

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

    public List<TelemetryData> getHistory(){
        return telemetryMapper.toTelemetryDto(telemetryRepository.findAll());

    }

}
