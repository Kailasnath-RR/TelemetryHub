package com.kailas.TelemetryHub.repository;

import com.kailas.TelemetryHub.entities.TelemetryEntity;
import com.kailas.TelemetryHub.model.TelemetryStat;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryEntity,Long> {

    Page<TelemetryEntity> findByadcValueGreaterThan(Integer adcMin, Pageable pageable);

    Page<TelemetryEntity> findByadcValueBetween(Integer min,Integer max,Pageable page);

    Page<TelemetryEntity> findByadcValueLessThan(Integer adcMax, Pageable pageable);

    @Query("""
            SELECT NEW com.kailas.TelemetryHub.model.TelemetryStat(
            AVG(t.adcValue),
            MIN(t.adcValue),
            MAX(t.adcValue),
            COUNT(t))
            FROM TelemetryEntity t
            WHERE (:from IS NULL OR t.receivedAt >=:from)
                       and
                  (:to IS NULL OR t.receivedAt<= :to)
                       and
                  (:minAdc IS NULL OR t.adcValue >=:minAdc)
                        and
                   (:maxAdc IS NULL OR t.adcValue <=:maxAdc)
           """)
    TelemetryStat findStats(
            @Param("from")LocalDateTime from,
            @Param("to")LocalDateTime to,
            @Param("minAdc")Integer minAdc,
            @Param("maxAdc")Integer maxAdc
            );

}
