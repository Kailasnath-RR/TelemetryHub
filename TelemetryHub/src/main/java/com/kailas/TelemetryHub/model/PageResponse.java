package com.kailas.TelemetryHub.model;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalPages,
        long totalElements,
        boolean hasNext,
        boolean hasPrevious
) {
}
