package com.kailas.TelemetryHub.mapper;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.model.TelemetryData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelemetryMapper {

    public TelemetryEntity toTelemetryEntity(TelemetryData dto){
                if(dto == null){
                    return null;
                }
                return TelemetryEntity.builder()
                        .count(dto.Count())
                        .adcValue(dto.AdcValue())
                        .samplePeriod(dto.SamplePeriod())
                        .receivedAt(dto.receivedAt()).build();
    }

    public List<TelemetryData> toTelemetryDto(List<TelemetryEntity> entityList){

        if(entityList ==null) return List.of();

        List<TelemetryData> data = new ArrayList<>();

        for(TelemetryEntity entity:entityList){
            TelemetryData dto = new TelemetryData(entity.getCount(),entity.getAdcValue(),entity.getSamplePeriod(),entity.getReceivedAt());
            data.add(dto);
        }

        return data;
    }
}
