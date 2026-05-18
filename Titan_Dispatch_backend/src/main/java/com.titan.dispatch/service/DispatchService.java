package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.JobSite;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.policy.SafetyInterlockPolicy;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.JobSiteRepository;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.web.dto.CompleteDispatchCommand;
import com.titan.dispatch.web.dto.CreateDispatchCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final EquipmentRepository equipmentRepo;
    private final OperatorRepository operatorRepo;
    private final JobSiteRepository jobSiteRepo;
    private final DispatchAllocationRepository dispatchRepo;
    private final SafetyInterlockPolicy safetyInterlockPolicy;

    @Transactional
    public DispatchAllocation createDispatch(CreateDispatchCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId()).orElseThrow();
        Operator operator = operatorRepo.findById(command.operatorId()).orElseThrow();

        safetyInterlockPolicy.validate(equipment, operator);
        equipment.setStatus(EquipmentStatus.DISPATCHED);

        DispatchAllocation allocation = DispatchAllocation.builder()
                .equipment(equipment)
                .operator(operator)
                .jobSiteId(command.jobSiteId())
                .startDate(command.startDate())
                .requiresHeavyTransport(command.requiresHeavyTransport())
                .status(DispatchStatus.ACTIVE)
                .build();

        return dispatchRepo.save(allocation);
    }

    @Transactional
    public void completeDispatch(UUID dispatchId, CompleteDispatchCommand command) {
        DispatchAllocation dispatch = dispatchRepo.findById(dispatchId).orElseThrow();
        if (dispatch.getStatus() != DispatchStatus.ACTIVE) {
            throw new IllegalStateException("Only active dispatches can be completed.");
        }

        Equipment equipment = dispatch.getEquipment();
        JobSite jobSite = jobSiteRepo.findById(dispatch.getJobSiteId()).orElseThrow();

        // Engine hours delta validation
        BigDecimal startHours = equipment.getCurrentEngineHours();
        if (command.endHours().compareTo(startHours) < 0) {
            throw new IllegalArgumentException("End hours cannot be less than start hours.");
        }

        // Calculate Cost
        BigDecimal hoursUsed = command.endHours().subtract(startHours);
        BigDecimal totalJobCost = hoursUsed.multiply(equipment.getInternalHourlyRate());

        // Update Job Site
        jobSite.setAccumulatedCost(jobSite.getAccumulatedCost().add(totalJobCost));

        // Update Equipment
        equipment.setCurrentEngineHours(command.endHours());
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        // Update Dispatch Record
        dispatch.setStatus(DispatchStatus.COMPLETED);

        jobSiteRepo.save(jobSite);
        equipmentRepo.save(equipment);
        dispatchRepo.save(dispatch);
    }
}