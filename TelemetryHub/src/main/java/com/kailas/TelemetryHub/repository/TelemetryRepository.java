package com.kailas.TelemetryHub.repository;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryEntity,Long> {

    Page<TelemetryEntity> findByadcValueGreaterThan(Integer adcMin, Pageable pageable);

    Page<TelemetryEntity> findByadcValueBetween(Integer min,Integer max,Pageable page);

}
