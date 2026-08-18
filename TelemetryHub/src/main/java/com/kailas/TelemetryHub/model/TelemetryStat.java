package com.kailas.TelemetryHub.model;

public record TelemetryStat(Double averageAdc,
                            Integer minAdc,
                            Integer maxAdc,
                            Long totalSamples
                                ) {
}
