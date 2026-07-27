package com.kailas.TelemetryHub.model;

public record TelemetryStat(Double averageAdc,
                            int minAdc,
                            int maxAdc,
                            Long totalSamples
                                ) {
}
