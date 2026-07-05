package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.DispatchLookupService;
import com.titan.dispatch.web.dto.EquipmentDropdown;
import com.titan.dispatch.web.dto.JobSiteDropdown;
import com.titan.dispatch.web.dto.OperatorDropdown;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dispatch/form-data")
@RequiredArgsConstructor
public class DispatchFormDataController {

    private final DispatchLookupService lookupService;

    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<EquipmentDropdown>> getEquipmentLookup() {
        return ResponseEntity.ok(lookupService.getAvailableEquipment());
    }

    @GetMapping("/operators")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<OperatorDropdown>> getOperatorLookup() {
        return ResponseEntity.ok(lookupService.getAvailableOperators());
    }

    @GetMapping("/job-sites")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    public ResponseEntity<List<JobSiteDropdown>> getJobSiteLookup() {
        return ResponseEntity.ok(lookupService.getActiveJobSites());
    }
}