package com.titan.dispatch.service;

import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.enums.OperatorStatus;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.JobSiteRepository;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.web.dto.EquipmentDropdown;
import com.titan.dispatch.web.dto.JobSiteDropdown;
import com.titan.dispatch.web.dto.OperatorDropdown;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispatchLookupService {

    private final EquipmentRepository equipmentRepository;
    private final OperatorRepository operatorRepository;
    private final JobSiteRepository jobSiteRepository;
    private final DispatchAllocationRepository dispatchAllocationRepository;

    private static final List<DispatchStatus> ACTIVE_STATUSES = List.of(
            DispatchStatus.ACTIVE, DispatchStatus.SCHEDULED, DispatchStatus.PENDING
    );

    public List<EquipmentDropdown> getAvailableEquipment() {
        List<UUID> busyEquipmentIds = dispatchAllocationRepository.findEquipmentIdsInStatuses(ACTIVE_STATUSES);
        LocalDate today = LocalDate.now();

        return equipmentRepository.findAll().stream()
                // Allow scheduling for MAINTENANCE vehicles, but block DOWN vehicles
                .filter(eq -> eq.getStatus() == EquipmentStatus.AVAILABLE || eq.getStatus() == EquipmentStatus.MAINTENANCE)
                .filter(eq -> !busyEquipmentIds.contains(eq.getId()))
                // Filter out equipment that is already expired today
                .filter(eq -> !eq.getInsuranceExpiration().isBefore(today))
                .map(eq -> {
                    // Append a warning label to the dropdown string for vehicles actively in the shop
                    String label = eq.getAssetTag() + (eq.getStatus() == EquipmentStatus.MAINTENANCE ? " (In Shop)" : "");
                    return new EquipmentDropdown(eq.getId(), label);
                })
                .collect(Collectors.toList());
    }

    public List<OperatorDropdown> getAvailableOperators() {
        List<UUID> busyOperatorIds = dispatchAllocationRepository.findOperatorIdsInStatuses(ACTIVE_STATUSES);
        LocalDate today = LocalDate.now();

        return operatorRepository.findAll().stream()
                .filter(op -> op.getStatus() == OperatorStatus.ACTIVE)
                .filter(op -> !busyOperatorIds.contains(op.getId()))
                // Filter out operators whose licenses are already expired today
                .filter(op -> !op.getLicenseExpiration().isBefore(today))
                .map(op -> new OperatorDropdown(op.getId(), op.getFirstName(), op.getLastName()))
                .collect(Collectors.toList());
    }

    public List<JobSiteDropdown> getActiveJobSites() {
        return jobSiteRepository.findAll().stream()
                .map(site -> new JobSiteDropdown(site.getId(), site.getProjectCode()))
                .collect(Collectors.toList());
    }
}