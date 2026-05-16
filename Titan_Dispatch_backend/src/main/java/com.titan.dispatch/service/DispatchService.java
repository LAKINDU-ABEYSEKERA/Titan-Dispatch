package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.policy.SafetyInterlockPolicy;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.web.dto.CreateDispatchCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final EquipmentRepository equipmentRepo;
    private final OperatorRepository operatorRepo;
    private final DispatchAllocationRepository dispatchRepo;
    private final SafetyInterlockPolicy safetyInterlockPolicy;

    @Transactional
    public DispatchAllocation createDispatch(CreateDispatchCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId()).orElseThrow();
        Operator operator = operatorRepo.findById(command.operatorId()).orElseThrow();

        // 1. Pure Domain Logic Validation
        safetyInterlockPolicy.validate(equipment, operator);

        // 2. State Mutation
        equipment.setStatus(EquipmentStatus.DISPATCHED);

        // 3. Persistence & Event Publishing
        DispatchAllocation allocation = DispatchAllocation.builder()
                .equipment(equipment)
                .operator(operator)
                .jobSiteId(command.jobSiteId())
                .startDate(command.startDate())
                .requiresHeavyTransport(command.requiresHeavyTransport())
                .build();

        return dispatchRepo.save(allocation);
    }
}