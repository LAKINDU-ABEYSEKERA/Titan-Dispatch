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

    public void validate(Equipment equipment, Operator operator, LocalDateTime requestedStartDate, LocalDateTime expectedEndDate) {

        if (expectedEndDate.isBefore(requestedStartDate) || expectedEndDate.isEqual(requestedStartDate)) {
            throw new SafetyInterlockException("The expected end date must be strictly after the start date.");
        }

        LocalDate dispatchStartDate = requestedStartDate.toLocalDate();
        LocalDate dispatchEndDate = expectedEndDate.toLocalDate(); // <-- This is the crucial missing piece!
        LocalDate today = LocalDate.now();

        // --- 1. OPERATOR TIMELINE CHECKS ---
        if (operator.getLicenseExpiration().isBefore(today)) {
            throw new SafetyInterlockException("Operator license is already expired as of today.");
        }

        // UPGRADED: Ensure the license is valid for the ENTIRE duration of the dispatch
        if (operator.getLicenseExpiration().isBefore(dispatchEndDate)) {
            throw new SafetyInterlockException(
                    "Operator license expires on " + operator.getLicenseExpiration() +
                            ", which is before the expected completion date of " + dispatchEndDate + "."
            );
        }

        if (dispatchRepo.hasOverlappingOperatorDispatch(operator.getId(), requestedStartDate, expectedEndDate, BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Operator is already booked for another dispatch during this exact time window.");
        }

        // --- 2. EQUIPMENT TIMELINE CHECKS ---
        if (equipment.getInsuranceExpiration().isBefore(today)) {
            throw new SafetyInterlockException("Equipment insurance is already expired as of today.");
        }

        // UPGRADED: Ensure the insurance is valid for the ENTIRE duration of the dispatch
        if (equipment.getInsuranceExpiration().isBefore(dispatchEndDate)) {
            throw new SafetyInterlockException(
                    "Equipment insurance expires on " + equipment.getInsuranceExpiration() +
                            ", which is before the expected completion date of " + dispatchEndDate + "."
            );
        }

        if (equipment.getStatus() == EquipmentStatus.DOWN) {
            throw new SafetyInterlockException("Equipment is critically DOWN and requires evaluation before future dispatching.");
        }

        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            if (equipment.getExpectedMaintenanceEndDate() == null) {
                throw new SafetyInterlockException("Equipment is in MAINTENANCE with no estimated completion date. Cannot safely schedule.");
            }
            if (!requestedStartDate.isAfter(equipment.getExpectedMaintenanceEndDate())) {
                throw new SafetyInterlockException(
                        "Equipment is in MAINTENANCE and is not expected to be released until " + equipment.getExpectedMaintenanceEndDate()
                );
            }
        }

        if (dispatchRepo.hasOverlappingEquipmentDispatch(equipment.getId(), requestedStartDate, expectedEndDate, BLOCKING_STATUSES)) {
            throw new SafetyInterlockException("Equipment is already deployed to another site during this exact time window.");
        }
    }
}