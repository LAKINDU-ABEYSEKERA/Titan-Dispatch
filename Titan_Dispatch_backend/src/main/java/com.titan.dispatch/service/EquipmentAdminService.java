package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.web.dto.EquipmentCommands.CreateEquipmentCommand;
import com.titan.dispatch.web.dto.EquipmentCommands.UpdateEquipmentCommand;
import com.titan.dispatch.web.dto.EquipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentAdminService {

    private final EquipmentRepository equipmentRepo;

    @Transactional(readOnly = true)
    public List<EquipmentResponse> getAllEquipment() {
        return equipmentRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EquipmentResponse createEquipment(CreateEquipmentCommand cmd) {
        if (equipmentRepo.existsByAssetTag(cmd.assetTag())) {
            throw new IllegalArgumentException("Asset tag already exists in the fleet.");
        }

        Equipment equipment = Equipment.builder()
                .assetTag(cmd.assetTag())
                .internalHourlyRate(cmd.internalHourlyRate())
                .insuranceExpiration(cmd.insuranceExpiration())
                // Forced System Defaults:
                .currentEngineHours(BigDecimal.ZERO)
                .status(EquipmentStatus.AVAILABLE)
                .build();

        return mapToResponse(equipmentRepo.save(equipment));
    }

    @Transactional
    public EquipmentResponse updateEquipment(UUID id, UpdateEquipmentCommand cmd) {
        Equipment equipment = equipmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        // Only update allowed fields. Notice how status/hours are untouched.
        equipment.setAssetTag(cmd.assetTag());
        equipment.setInternalHourlyRate(cmd.internalHourlyRate());
        equipment.setInsuranceExpiration(cmd.insuranceExpiration());

        return mapToResponse(equipmentRepo.save(equipment));
    }

    @Transactional
    public void deleteEquipment(UUID id) {
        Equipment equipment = equipmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found"));

        // This triggers Hibernate @SoftDelete. The row stays, 'deleted' becomes true.
        equipmentRepo.delete(equipment);
    }

    private EquipmentResponse mapToResponse(Equipment e) {
        return new EquipmentResponse(
                e.getId(),
                e.getAssetTag(),
                e.getStatus(),
                e.getCurrentEngineHours(),
                e.getInternalHourlyRate(),
                e.getInsuranceExpiration()
        );
    }
}