package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.parser.SerialParser;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

@Service
public class TelemetryService {

    private static Logger logger = LoggerFactory.getLogger(TelemetryService.class);
    private TelemetryStatus latestStatus;  //holds the current status of the machine
    private TelemetryData latestData;

    private Map<Integer,TelemetryData> dataHistory = new HashMap<>();

    private final SerialParser serialParser;

    public TelemetryService(SerialParser serialParser){

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
                }

            }
            if(Action.equals("DATA")){
                TelemetryData parsedData = serialParser.parseData(data);

                if (parsedData != null) {
                    latestData = parsedData;
                    dataHistory.put(parsedData.Count(), parsedData);
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

    public Map<Integer, TelemetryData> getHistory(){
        return dataHistory;

    }

}
