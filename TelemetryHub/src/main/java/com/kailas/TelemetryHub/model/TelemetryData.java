package com.kailas.TelemetryHub.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record TelemetryData(int Count,
                            int AdcValue,
                            int SamplePeriod,

                            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
                            LocalDateTime receivedAt){
}


//TYPE=DATA,COUNT=0,ADC=216,SAMPLE_PERIOD=10000