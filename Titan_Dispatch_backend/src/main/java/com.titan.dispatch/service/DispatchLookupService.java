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

        return equipmentRepository.findAll().stream()
                .filter(eq -> eq.getStatus() == EquipmentStatus.AVAILABLE)
                .filter(eq -> !busyEquipmentIds.contains(eq.getId()))
                .map(eq -> new EquipmentDropdown(eq.getId(), eq.getAssetTag()))
                .collect(Collectors.toList());
    }

    public List<OperatorDropdown> getAvailableOperators() {
        List<UUID> busyOperatorIds = dispatchAllocationRepository.findOperatorIdsInStatuses(ACTIVE_STATUSES);

        return operatorRepository.findAll().stream()
                .filter(op -> op.getStatus() == OperatorStatus.ACTIVE)
                .filter(op -> !busyOperatorIds.contains(op.getId()))
                .map(op -> new OperatorDropdown(op.getId(), op.getFirstName(), op.getLastName()))
                .collect(Collectors.toList());
    }

    public List<JobSiteDropdown> getActiveJobSites() {
        return jobSiteRepository.findAll().stream()
                .map(site -> new JobSiteDropdown(site.getId(), site.getProjectCode()))
                .collect(Collectors.toList());
    }
}