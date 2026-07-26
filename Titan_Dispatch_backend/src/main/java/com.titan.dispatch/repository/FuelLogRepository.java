package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FuelLogRepository extends JpaRepository<FuelLog, UUID> {
    List<FuelLog> findByEquipmentIdOrderByFillDateDesc(UUID equipmentId);

    // NEW: Aggregates fuel costs for a specific machine during a specific time window
    @Query("""
        SELECT COALESCE(SUM(f.totalCost), 0) 
        FROM FuelLog f 
        WHERE f.equipment.id = :equipmentId 
        AND f.fillDate >= :startDate 
        AND f.fillDate <= :endDate
    """)
    BigDecimal calculateFuelCostForPeriod(
            @Param("equipmentId") UUID equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}