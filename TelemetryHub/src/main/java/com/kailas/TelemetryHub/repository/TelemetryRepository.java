package com.kailas.TelemetryHub.repository;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryEntity,Long> {


}
