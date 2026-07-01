package com.titandispatch.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dispatch/form-data")
public class DispatchFormDataController {

    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<EquipmentLookupDTO>> getEquipmentLookup() {
        return ResponseEntity.ok(dispatchService.getAvailableEquipment());
    }

    @GetMapping("/operators")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<OperatorLookupDTO>> getOperatorLookup() {
        return ResponseEntity.ok(dispatchService.getAvailableOperators());
    }

    @GetMapping("/job-sites")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<JobSiteLookupDTO>> getJobSiteLookup() {
        return ResponseEntity.ok(dispatchService.getActiveJobSites());
    }
}