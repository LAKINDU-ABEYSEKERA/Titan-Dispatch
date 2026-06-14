package com.titan.dispatch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.titan.dispatch.domain.entity.DispatchAllocation;
import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.JobSite;
import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.entity.OutboxEvent;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import com.titan.dispatch.domain.policy.SafetyInterlockPolicy;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.JobSiteRepository;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.repository.OutboxEventRepository;
import com.titan.dispatch.web.dto.CompleteDispatchCommand;
import com.titan.dispatch.web.dto.CreateDispatchCommand;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private final EquipmentRepository equipmentRepo;
    private final OperatorRepository operatorRepo;
    private final JobSiteRepository jobSiteRepo;
    private final DispatchAllocationRepository dispatchRepo;
    private final SafetyInterlockPolicy safetyInterlockPolicy;
    private final OutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    // NEW: Injected to track business metrics
    private final MeterRegistry meterRegistry;

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

        DispatchAllocation savedAllocation = dispatchRepo.save(allocation);

        // NEW: Increment the active dispatch counter
        meterRegistry.counter("titan.dispatch.created").increment();
        return savedAllocation;
    }

    @Transactional
    public void completeDispatch(UUID dispatchId, CompleteDispatchCommand command) {
        DispatchAllocation dispatch = dispatchRepo.findById(dispatchId).orElseThrow();
        if (dispatch.getStatus() != DispatchStatus.ACTIVE) {
            throw new IllegalStateException("Only active dispatches can be completed.");
        }

        Equipment equipment = dispatch.getEquipment();
        JobSite jobSite = jobSiteRepo.findById(dispatch.getJobSiteId()).orElseThrow();

        BigDecimal startHours = equipment.getCurrentEngineHours();
        if (command.endHours().compareTo(startHours) < 0) {
            throw new IllegalArgumentException("End hours cannot be less than start hours.");
        }

        BigDecimal hoursUsed = command.endHours().subtract(startHours);
        BigDecimal totalJobCost = hoursUsed.multiply(equipment.getInternalHourlyRate());

        jobSite.setAccumulatedCost(jobSite.getAccumulatedCost().add(totalJobCost));
        equipment.setCurrentEngineHours(command.endHours());
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        dispatch.setStatus(DispatchStatus.COMPLETED);
        dispatch.setEndDate(LocalDateTime.now());

        jobSiteRepo.save(jobSite);
        equipmentRepo.save(equipment);
        dispatchRepo.save(dispatch);

        try {
            String payload = objectMapper.writeValueAsString(command);
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("DISPATCH_ALLOCATION")
                    .aggregateId(dispatch.getId())
                    .eventType("DISPATCH_COMPLETED")
                    .payload(payload)
                    .build();
            outboxRepo.save(event);

            // NEW: Track completed dispatches and record the total cost generated
            meterRegistry.counter("titan.dispatch.completed").increment();
            meterRegistry.summary("titan.dispatch.revenue").record(totalJobCost.doubleValue());

            log.info("Dispatch {} completed. Cost: {}. Outbox event queued.", dispatchId, totalJobCost);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize dispatch completion event", e);
            throw new RuntimeException("System error securing dispatch completion logic");
        }
    }
}