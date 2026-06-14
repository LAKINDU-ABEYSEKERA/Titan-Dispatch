package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.FuelLog;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.FuelLogRepository;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.web.dto.CreateFuelLogCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FuelService {

    private final FuelLogRepository fuelLogRepo;
    private final EquipmentRepository equipmentRepo;
    private final OperatorRepository operatorRepo;

    @Transactional
    public void submitFuelLog(CreateFuelLogCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        Operator operator = operatorRepo.findById(command.operatorId())
                .orElseThrow(() -> new IllegalArgumentException("Operator not found"));

        FuelLog log = FuelLog.builder()
                .equipment(equipment)
                .operator(operator)
                .gallonsAdded(command.gallonsAdded())
                .totalCost(command.totalCost())
                .engineHoursAtFillUp(command.engineHoursAtFillUp())
                .fillDate(command.fillDate())
                .build();

        fuelLogRepo.save(log);

        // Update equipment engine hours if the fuel log reflects newer usage
        if (command.engineHoursAtFillUp().compareTo(equipment.getCurrentEngineHours()) > 0) {
            equipment.setCurrentEngineHours(command.engineHoursAtFillUp());
            equipmentRepo.save(equipment);
        }
    }
}