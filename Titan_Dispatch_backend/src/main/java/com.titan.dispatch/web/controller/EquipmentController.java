package com.titan.dispatch.web.controller;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.web.dto.EquipmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentRepository equipmentRepo;

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment() {
        List<EquipmentResponse> responses = equipmentRepo.findAll().stream()
                .map(e -> new EquipmentResponse(
                        e.getId(), e.getAssetTag(), e.getStatus(),
                        e.getCurrentEngineHours(), e.getInternalHourlyRate(), e.getInsuranceExpiration()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Additional CRUD omitted for brevity, protected by hasRole('ADMIN')
}