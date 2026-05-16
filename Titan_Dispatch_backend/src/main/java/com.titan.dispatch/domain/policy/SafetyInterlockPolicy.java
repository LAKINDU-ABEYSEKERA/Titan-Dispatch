package com.titan.dispatch.domain.policy;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.exception.SafetyInterlockException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SafetyInterlockPolicy {

    public void validate(Equipment equipment, Operator operator) {
        if (operator.getLicenseExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Operator license is expired.");
        }
        if (equipment.getStatus() == EquipmentStatus.MAINTENANCE || equipment.getStatus() == EquipmentStatus.DOWN) {
            throw new SafetyInterlockException("Equipment is currently unavailable for dispatch.");
        }
        if (equipment.getInsuranceExpiration().isBefore(LocalDate.now())) {
            throw new SafetyInterlockException("Equipment insurance is expired.");
        }
    }
}