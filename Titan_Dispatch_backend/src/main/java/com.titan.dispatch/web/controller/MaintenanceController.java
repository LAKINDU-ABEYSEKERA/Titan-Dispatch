package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.MaintenanceService;
import com.titan.dispatch.repository.MaintenanceLogRepository;
import com.titan.dispatch.web.dto.ActiveMaintenanceResponse;
import com.titan.dispatch.web.dto.CreateMaintenanceLogCommand;
import com.titan.dispatch.web.dto.MaintenanceLogResponse;
import com.titan.dispatch.web.dto.SendToShopCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final MaintenanceLogRepository maintenanceRepo;

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC')")
    @PostMapping
    public ResponseEntity<Void> submitLog(@Valid @RequestBody CreateMaintenanceLogCommand request) {
        maintenanceService.submitMaintenanceLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @GetMapping("/active")
    public ResponseEntity<List<ActiveMaintenanceResponse>> getActiveShopRoster() {
        return ResponseEntity.ok(maintenanceService.getActiveShopRoster());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @PostMapping("/{equipmentId}/shop")
    public ResponseEntity<Void> sendToShop(
            @PathVariable UUID equipmentId,
            @Valid @RequestBody SendToShopCommand command) {

        maintenanceService.sendToShop(equipmentId, command.expectedEndDate());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @GetMapping("/{equipmentId}")
    public ResponseEntity<List<MaintenanceLogResponse>> getHistory(@PathVariable UUID equipmentId) {
        List<MaintenanceLogResponse> response = maintenanceRepo.findByEquipmentIdOrderByServiceDateDesc(equipmentId)
                .stream()
                .map(l -> new MaintenanceLogResponse(
                        l.getId(),
                        l.getEquipment().getId(),
                        l.getEquipment().getAssetTag(), // FIXED: Added Asset Tag
                        l.getServiceDate(),
                        l.getHoursAtService(),
                        l.getServiceType().name(),      // FIXED: Converted Enum to String
                        l.getTotalCost(),
                        l.getNotes()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<MaintenanceLogResponse>> getAllHistory() {
        return ResponseEntity.ok(maintenanceService.getAllLogs());
    }
}