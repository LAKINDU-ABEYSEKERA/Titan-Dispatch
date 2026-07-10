package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.enums.DispatchStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchAllocationRepository extends JpaRepository<DispatchAllocation, UUID> {

    boolean existsByEquipmentIdAndStatusIn(UUID equipmentId, List<DispatchStatus> statuses);
    boolean existsByOperatorIdAndStatusIn(UUID operatorId, List<DispatchStatus> statuses);

    // 2. For the Dropdowns (UX)
    @Query("SELECT d.equipment.id FROM DispatchAllocation d WHERE d.status IN :statuses")
    List<UUID> findEquipmentIdsInStatuses(@Param("statuses") List<DispatchStatus> statuses);

    @Query("SELECT d.operator.id FROM DispatchAllocation d WHERE d.status IN :statuses")
    List<UUID> findOperatorIdsInStatuses(@Param("statuses") List<DispatchStatus> statuses);

    Optional<DispatchAllocation> findByEquipmentIdAndStatus(UUID equipmentId, DispatchStatus status);
    List<DispatchAllocation> findByStatusOrderByStartDateDesc(DispatchStatus status);
    List<DispatchAllocation> findAllByOrderByStartDateDesc();
}