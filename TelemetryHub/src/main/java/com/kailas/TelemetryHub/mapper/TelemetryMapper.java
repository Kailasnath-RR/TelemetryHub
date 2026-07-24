package com.kailas.TelemetryHub.mapper;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.model.PageResponse;
import com.kailas.TelemetryHub.model.TelemetryData;
import org.springframework.data.domain.Page;
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


    public TelemetryData toTelemetryDto(TelemetryEntity entity) {

        return new TelemetryData(
                entity.getCount(),
                entity.getAdcValue(),
                entity.getSamplePeriod(),
                entity.getReceivedAt()
        );
    }
    public PageResponse<TelemetryData> toPageResponse(Page<TelemetryData> page){
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious());
    }
}
