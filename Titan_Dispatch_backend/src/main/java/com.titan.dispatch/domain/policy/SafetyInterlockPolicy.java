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
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SafetyInterlockPolicy {

    private final DispatchAllocationRepository dispatchRepo;

    private static final List<DispatchStatus> BLOCKING_STATUSES = List.of(
            DispatchStatus.ACTIVE, DispatchStatus.SCHEDULED, DispatchStatus.PENDING
    );

    // UPGRADED: Now requires the requested start date of the dispatch
    public void validate(Equipment equipment, Operator operator, LocalDateTime requestedStartDate) {

        if (operator.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Operator license is expired.");
        }

        if (dispatchRepo.existsByOperatorIdAndStatusIn(operator.getId(), BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Operator is already assigned to an active or scheduled dispatch.");
        }

        if (equipment.getInsuranceExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Equipment insurance is expired.");
        }

        // Hard block for downed equipment
        if (equipment.getStatus() == EquipmentStatus.DOWN) {
            throw new SafetyInterlockException("Equipment is critically DOWN and requires evaluation before future dispatching.");
        }

        // NEW: Time-Aware Maintenance Calculation
        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            if (equipment.getExpectedMaintenanceEndDate() == null) {
                throw new SafetyInterlockException("Equipment is in MAINTENANCE with no estimated completion date. Cannot safely schedule.");
            }

            if (requestedStartDate.isBefore(equipment.getExpectedMaintenanceEndDate())) {
                throw new SafetyInterlockException(
                        "Equipment is in MAINTENANCE and is not expected to be released until " + equipment.getExpectedMaintenanceEndDate()
                );
            }
        }

        if (dispatchRepo.existsByEquipmentIdAndStatusIn(equipment.getId(), BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Equipment is already deployed to another active or scheduled dispatch.");
        }
    }
}