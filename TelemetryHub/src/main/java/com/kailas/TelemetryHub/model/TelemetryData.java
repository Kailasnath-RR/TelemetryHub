package com.kailas.TelemetryHub.model;

public record TelemetryData(long Count,
                            int AdcValue,
                            int SamplePeriod){
}


//TYPE=DATA,COUNT=0,ADC=216,SAMPLE_PERIOD=10000