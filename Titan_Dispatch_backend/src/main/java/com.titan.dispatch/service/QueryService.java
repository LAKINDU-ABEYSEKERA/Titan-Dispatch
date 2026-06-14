package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.JobSite;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.enums.OperatorStatus;
import com.titan.dispatch.repository.*;
import com.titan.dispatch.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryService {

    private final EquipmentRepository equipmentRepo;
    private final OperatorRepository operatorRepo;
    private final JobSiteRepository jobSiteRepo;
    private final DispatchAllocationRepository dispatchRepo;

    public List<EquipmentDropdownResponse> getAvailableEquipment() {
        return equipmentRepo.findByStatus(EquipmentStatus.AVAILABLE).stream()
                .map(e -> new EquipmentDropdownResponse(e.getId(), e.getAssetTag()))
                .collect(Collectors.toList());
    }

    public List<OperatorDropdownResponse> getActiveOperators() {
        return operatorRepo.findByStatus(OperatorStatus.ACTIVE).stream()
                .map(o -> new OperatorDropdownResponse(o.getId(), o.getFirstName(), o.getLastName()))
                .collect(Collectors.toList());
    }

    public List<JobSiteDropdownResponse> getAllJobSites() {
        return jobSiteRepo.findAll().stream()
                .map(j -> new JobSiteDropdownResponse(j.getId(), j.getProjectCode()))
                .collect(Collectors.toList());
    }

    public List<DispatchSummaryResponse> getDispatches(DispatchStatus status) {
        var allocations = status != null ?
                dispatchRepo.findByStatusOrderByStartDateDesc(status) :
                dispatchRepo.findAllByOrderByStartDateDesc();

        return allocations.stream().map(d -> {
            JobSite site = jobSiteRepo.findById(d.getJobSiteId()).orElseThrow();
            return new DispatchSummaryResponse(
                    d.getId(),
                    d.getEquipment().getAssetTag(),
                    d.getOperator().getFirstName() + " " + d.getOperator().getLastName(),
                    site.getProjectCode(),
                    d.getStatus(),
                    d.getStartDate(),
                    d.getEndDate()
            );
        }).collect(Collectors.toList());
    }
}