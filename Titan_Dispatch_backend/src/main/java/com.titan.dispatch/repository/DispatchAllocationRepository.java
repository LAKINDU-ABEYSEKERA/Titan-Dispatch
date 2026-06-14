package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.enums.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DispatchAllocationRepository extends JpaRepository<DispatchAllocation, UUID> {
    Optional<DispatchAllocation> findByEquipmentIdAndStatus(UUID equipmentId, DispatchStatus status);
    List<DispatchAllocation> findByStatusOrderByStartDateDesc(DispatchStatus status);
    List<DispatchAllocation> findAllByOrderByStartDateDesc();
}