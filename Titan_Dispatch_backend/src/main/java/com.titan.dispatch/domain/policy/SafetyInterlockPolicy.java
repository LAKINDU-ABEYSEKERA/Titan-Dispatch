package com.titan.dispatch.domain.policy;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.exception.SafetyInterlockException;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SafetyInterlockPolicy {

    private final DispatchAllocationRepository dispatchRepo;

    // A resource is considered "busy" if it is in any of these states
    private static final List<DispatchStatus> BLOCKING_STATUSES = List.of(
            DispatchStatus.ACTIVE, DispatchStatus.SCHEDULED, DispatchStatus.PENDING
    );

    public void validate(Equipment equipment, Operator operator) {

        // --- 1. OPERATOR CHECKS ---
        if (operator.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Operator license is expired.");
        }

        // NEW: Concurrency Check
        if (dispatchRepo.existsByOperatorIdAndStatusIn(operator.getId(), BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Operator is already assigned to an active or scheduled dispatch.");
        }

        // --- 2. EQUIPMENT CHECKS ---
        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE || equipment.getStatus() == EquipmentStatus.DOWN) {
            throw new SafetyInterlockException("Equipment is currently unavailable for dispatch.");
        }

        if (equipment.getInsuranceExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Equipment insurance is expired.");
        }

        // NEW: Concurrency Check
        if (dispatchRepo.existsByEquipmentIdAndStatusIn(equipment.getId(), BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Equipment is already deployed to another active or scheduled dispatch.");
        }
    }
}