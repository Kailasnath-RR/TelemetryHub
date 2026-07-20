package com.kailas.TelemetryHub.parser;

import com.kailas.TelemetryHub.model.TelemetryData;
import com.kailas.TelemetryHub.model.TelemetryStatus;
public class SerialParser {

    private TelemetryData parseData(String DataLine){
        try{
        long count = 0;
        int adc = 0;
        int sample_period = 0;

        String[] WholeToken = DataLine.split(","); //splits it into COUNT=XXX,ADC=XXX and so on

        if(WholeToken.length < 3) return null;

        for(String t:WholeToken){
            String[] token = t.split("=");
            String data = token[1].trim();
            if(token.length == 2){
                switch (token[0].trim().toUpperCase()){
                    case "COUNT" : count = Long.parseLong(data);
                                    break;
                    case "ADC" : adc = Integer.parseInt(data);
                                    break;
                    case "SAMPLE_PERIOD": sample_period =Integer.parseInt(data);
                                            break;

                }
            }
        }
        return new TelemetryData(count,adc,sample_period);
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    private TelemetryStatus parseStatus(String StatusLine){

        String[] Token = StatusLine.split("=");
        if(Token.length < 2)return null;
        return new TelemetryStatus(Token[1].trim());

    }

    public void parse(String Line){
        if(Line == null || !Line.contains(",")){return;}


        int IndexOfComma = Line.indexOf(","); //extracts the index of the comma before count
        String Token = Line.substring(0,IndexOfComma);

        String[] Type = Token.split("=");
        if(Type.length <2) return;

        String ActionType = Type[1].trim().toUpperCase();

        String InfoLine = Line.substring(IndexOfComma+1);   //holds the info after the type is extracted

        switch(ActionType){
            case "DATA" : parseData(InfoLine);
                            break;
            case "STATUS": parseStatus(InfoLine);
                            break;

        }
    }
}
