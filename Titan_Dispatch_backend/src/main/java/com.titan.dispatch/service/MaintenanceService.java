package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.MaintenanceLog;
import com.titan.dispatch.domain.enums.EquipmentStatus;
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

    // --- NEW: Put the equipment into the shop and lock the timeline ---
    @Transactional
    public void sendToShop(UUID equipmentId, LocalDateTime expectedEndDate) {
        Equipment equipment = equipmentRepo.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        if (equipment.getStatus() == EquipmentStatus.DISPATCHED) {
            throw new IllegalStateException("Cannot send equipment to the shop while it is actively deployed on a job site.");
        }

        equipment.setStatus(EquipmentStatus.MAINTENANCE);
        equipment.setMaintenanceStartDate(LocalDateTime.now()); // NEW: Stamp the start time
        equipment.setExpectedMaintenanceEndDate(expectedEndDate);
        equipmentRepo.save(equipment);
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

        // Return equipment to AVAILABLE and clear the timeline lock
        if (equipment.getStatus() == EquipmentStatus.DOWN || equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            equipment.setStatus(EquipmentStatus.AVAILABLE);
            equipment.setMaintenanceStartDate(null);       // NEW: Clear the start date
            equipment.setExpectedMaintenanceEndDate(null);
            equipmentRepo.save(equipment);
        }
    }

    @Transactional(readOnly = true)
    public List<MaintenanceLogResponse> getAllLogs() {
        // UPGRADED: Order by date descending by default and map the Asset Tag
        return maintenanceLogRepo.findAll().stream()
                .sorted((a, b) -> b.getServiceDate().compareTo(a.getServiceDate()))
                .map(l -> new MaintenanceLogResponse(
                        l.getId(),
                        l.getEquipment().getId(),
                        l.getEquipment().getAssetTag(), // Pulled directly from the joined entity
                        l.getServiceDate(),
                        l.getHoursAtService(),
                        l.getServiceType().name(),
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