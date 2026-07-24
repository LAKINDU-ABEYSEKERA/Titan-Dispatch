package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.enums.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchAllocationRepository extends JpaRepository<DispatchAllocation, UUID> {

    // --- Predictive Overlap Queries ---
    @Query("""
        SELECT COUNT(d) > 0 FROM DispatchAllocation d 
        WHERE d.equipment.id = :equipmentId 
        AND d.status IN :statuses 
        AND d.startDate < :newEndDate 
        AND d.expectedEndDate > :newStartDate
    """)
    boolean hasOverlappingEquipmentDispatch(
            @Param("equipmentId") UUID equipmentId,
            @Param("newStartDate") LocalDateTime newStartDate,
            @Param("newEndDate") LocalDateTime newEndDate,
            @Param("statuses") List<DispatchStatus> statuses);

    @Query("""
        SELECT COUNT(d) > 0 FROM DispatchAllocation d 
        WHERE d.operator.id = :operatorId 
        AND d.status IN :statuses 
        AND d.startDate < :newEndDate 
        AND d.expectedEndDate > :newStartDate
    """)
    boolean hasOverlappingOperatorDispatch(
            @Param("operatorId") UUID operatorId,
            @Param("newStartDate") LocalDateTime newStartDate,
            @Param("newEndDate") LocalDateTime newEndDate,
            @Param("statuses") List<DispatchStatus> statuses);

    // UX Dropdown queries
    @Query("SELECT d.equipment.id FROM DispatchAllocation d WHERE d.status IN :statuses")
    List<UUID> findEquipmentIdsInStatuses(@Param("statuses") List<DispatchStatus> statuses);

    @Query("SELECT d.operator.id FROM DispatchAllocation d WHERE d.status IN :statuses")
    List<UUID> findOperatorIdsInStatuses(@Param("statuses") List<DispatchStatus> statuses);

    Optional<DispatchAllocation> findByEquipmentIdAndStatus(UUID equipmentId, DispatchStatus status);
    List<DispatchAllocation> findByStatusOrderByStartDateDesc(DispatchStatus status);
    List<DispatchAllocation> findAllByOrderByStartDateDesc();

    @Query("""
        SELECT d FROM DispatchAllocation d 
        WHERE d.equipment.id = :equipmentId 
        AND d.status IN ('SCHEDULED', 'PENDING') 
        AND d.startDate < :maintenanceEndDate
    """)
    List<DispatchAllocation> findDispatchesAtRisk(@Param("equipmentId") UUID equipmentId, @Param("maintenanceEndDate") LocalDateTime maintenanceEndDate);

    // NEW: The Watchdog Recovery Query
    List<DispatchAllocation> findAllByEquipmentIdAndStatus(UUID equipmentId, DispatchStatus status);
}
