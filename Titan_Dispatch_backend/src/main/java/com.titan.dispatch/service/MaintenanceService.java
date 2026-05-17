package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.MaintenanceLog;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.MaintenanceLogRepository;
import com.titan.dispatch.web.dto.CreateMaintenanceLogCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceLogRepository maintenanceLogRepo;
    private final EquipmentRepository equipmentRepo;

    @Transactional
    public void submitMaintenanceLog(CreateMaintenanceLogCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId()).orElseThrow();

        MaintenanceLog log = MaintenanceLog.builder()
                .equipment(equipment)
                .serviceDate(command.serviceDate())
                .hoursAtService(command.hoursAtService())
                .serviceType(command.serviceType())
                .totalCost(command.totalCost())
                .notes(command.notes())
                .build();

        maintenanceLogRepo.save(log);

        // Optional logic: Return equipment to AVAILABLE if it was DOWN
        if (equipment.getStatus() == EquipmentStatus.DOWN || equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            equipmentRepo.save(equipment);
        }
    }
}