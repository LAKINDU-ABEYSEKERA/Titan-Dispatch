package com.titan.dispatch.service;

import com.titan.dispatch.web.dto.EquipmentDropdown;
import com.titan.dispatch.web.dto.JobSiteDropdown;
import com.titan.dispatch.web.dto.OperatorDropdown;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.JobSiteRepository;
import com.titan.dispatch.repository.OperatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispatchLookupService {

    private final EquipmentRepository equipmentRepository;
    private final OperatorRepository operatorRepository;
    private final JobSiteRepository jobSiteRepository;

    @Transactional(readOnly = true)
    public List<EquipmentDropdown> getAvailableEquipment() {
        return equipmentRepository.findAll().stream()
                .map(eq -> new EquipmentDropdown(eq.getId(), eq.getAssetTag()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OperatorDropdown> getAvailableOperators() {
        return operatorRepository.findAll().stream()
                .map(op -> new OperatorDropdown(op.getId(), op.getFirstName(), op.getLastName()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobSiteDropdown> getActiveJobSites() {
        return jobSiteRepository.findAll().stream()
                .map(site -> new JobSiteDropdown(site.getId(), site.getProjectCode()))
                .collect(Collectors.toList());
    }
}