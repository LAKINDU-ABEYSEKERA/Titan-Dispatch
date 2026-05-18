package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.MaintenanceService;
import com.titan.dispatch.web.dto.CreateMaintenanceLogCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC')")
    @PostMapping
    public ResponseEntity<Void> submitLog(@Valid @RequestBody CreateMaintenanceLogCommand request) {
        maintenanceService.submitMaintenanceLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}