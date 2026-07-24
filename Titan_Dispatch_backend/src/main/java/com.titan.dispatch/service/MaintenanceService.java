package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.MaintenanceLog;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.MaintenanceLogRepository;
import com.titan.dispatch.web.dto.ActiveMaintenanceResponse;
import com.titan.dispatch.web.dto.CreateMaintenanceLogCommand;
import com.titan.dispatch.web.dto.MaintenanceLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final EquipmentRepository equipmentRepo;
    private final MaintenanceLogRepository maintenanceLogRepo;
    private final DispatchAllocationRepository dispatchRepo;

    // --- NEW: Put the equipment into the shop and lock the timeline ---
    @Transactional
    public void sendToShop(UUID equipmentId, LocalDateTime expectedEndDate) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        // OVERRIDE: Allowed to go to shop unless it is actively driving on a site right now
        if (equipment.getStatus() == EquipmentStatus.DISPATCHED) {
            throw new IllegalStateException("Cannot send equipment to the shop while it is actively deployed on a job site. Complete or cancel the active dispatch first.");
        }

        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipment.setMaintenanceStartDate(LocalDateTime.now());
        equipment.setExpectedMaintenanceEndDate(expectedEndDate);
        equipmentRepo.save(equipment);

        // WATCHDOG SCANNER: Flag any future dispatches that this repair overlaps with
        List<DispatchAllocation> atRiskDispatches = dispatchRepo.findDispatchesAtRisk(equipmentId, expectedEndDate);
        for (DispatchAllocation dispatch : atRiskDispatches) {
            dispatch.setStatus(DispatchStatus.AT_RISK);
            dispatchRepo.save(dispatch);
        }
    }

    @Transactional
    public void submitMaintenanceLog(CreateMaintenanceLogCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        MaintenanceLog log = MaintenanceLog.builder()
                .equipment(equipment)
                .serviceDate(command.serviceDate())
                .hoursAtService(command.hoursAtService())
                .serviceType(command.serviceType())
                .totalCost(command.totalCost())
                .notes(command.notes())
                .build();
        maintenanceLogRepo.save(log);

        if (equipment.getStatus() == EquipmentStatus.DOWN || equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            equipment.setMaintenanceStartDate(null);
            equipment.setExpectedMaintenanceEndDate(null);
            equipmentRepo.save(equipment);
        }

        // WATCHDOG RECOVERY: Repair finished early! Revert AT_RISK dispatches back to SCHEDULED
        List<DispatchAllocation> recoveredDispatches = dispatchRepo.findAllByEquipmentIdAndStatus(equipment.getId(), DispatchStatus.AT_RISK);
        for (DispatchAllocation dispatch : recoveredDispatches) {
            dispatch.setStatus(DispatchStatus.SCHEDULED);
            dispatchRepo.save(dispatch);
        }
    }

    @Transactional(readOnly = true)
    public List<MaintenanceLogResponse> getAllLogs() {
        return maintenanceLogRepo.findAll().stream()
                .sorted((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()))
                .map(l -> new MaintenanceLogResponse(
                        l.getId(),
                        // THE FIX: Null-safe checks for soft-deleted equipment
                        l.getEquipment() != null ? l.getEquipment().getId() : null,
                        l.getEquipment() != null ? l.getEquipment().getAssetTag() : "[Retired Asset]",
                        l.getServiceDate(),
                        l.getHoursAtService(),
                        l.getServiceType() != null ? l.getServiceType().name() : "UNKNOWN",
                        l.getTotalCost(),
                        l.getNotes()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActiveMaintenanceResponse> getActiveShopRoster() {
        return equipmentRepo.findAll().stream()
                .filter(e -> e.getStatus() == EquipmentStatus.MAINTENANCE)
                .map(e -> new ActiveMaintenanceResponse(
                        e.getId(),
                        e.getAssetTag(),
                        e.getMaintenanceStartDate(), // NEW: Map it to the DTO
                        e.getExpectedMaintenanceEndDate()
                ))
                .collect(Collectors.toList());
    }
}