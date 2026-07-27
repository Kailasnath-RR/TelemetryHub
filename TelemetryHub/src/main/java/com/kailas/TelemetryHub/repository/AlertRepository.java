package com.kailas.TelemetryHub.repository;

import com.kailas.TelemetryHub.entities.TelemetryAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<TelemetryAlert,Long> {
}
