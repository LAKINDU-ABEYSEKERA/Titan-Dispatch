package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.EquipmentAdminService;
import com.titan.dispatch.web.dto.EquipmentCommands.CreateEquipmentCommand;
import com.titan.dispatch.web.dto.EquipmentCommands.UpdateEquipmentCommand;
import com.titan.dispatch.web.dto.EquipmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    // SWAPPED: Using the Service instead of the Repository directly
    private final EquipmentAdminService equipmentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    // --- NEW ADMIN CRUD ENDPOINTS ---

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(@Valid @RequestBody CreateEquipmentCommand cmd) {
        return ResponseEntity.status(HttpStatus.CREATED).body(equipmentService.createEquipment(cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> updateEquipment(@PathVariable UUID id, @Valid @RequestBody UpdateEquipmentCommand cmd) {
        return ResponseEntity.ok(equipmentService.updateEquipment(id, cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable UUID id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}