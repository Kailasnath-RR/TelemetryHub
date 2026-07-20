package com.kailas.TelemetryHub.service;

import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
import com.kailas.TelemetryHub.parser.SerialParser;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class TelemetryService {

    private TelemetryStatus latestStatus;
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

}
