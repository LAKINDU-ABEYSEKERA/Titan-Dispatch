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
    private final MeterRegistry meterRegistry;

    @Transactional
    public DispatchAllocation createDispatch(CreateDispatchCommand command) {
        Equipment equipment = equipmentRepo.findById(command.equipmentId()).orElseThrow();
        Operator operator = operatorRepo.findById(command.operatorId()).orElseThrow();

        // Upgraded validation passes both dates
        safetyInterlockPolicy.validate(equipment, operator, command.startDate(), command.expectedEndDate());

        // Check if the job is starting today or in the future
        DispatchStatus initialStatus = command.startDate().isAfter(LocalDateTime.now())
                ? DispatchStatus.SCHEDULED
                : DispatchStatus.ACTIVE;

        // THE CRITICAL FIX: We ONLY lock the equipment if the job is starting RIGHT NOW.
        // If the job is SCHEDULED for the future, the vehicle status remains AVAILABLE.
        if (initialStatus == DispatchStatus.ACTIVE) {
            equipment.setStatus(EquipmentStatus.DISPATCHED);
        }

        DispatchAllocation allocation = DispatchAllocation.builder()
                .equipment(equipment)
                .operator(operator)
                .jobSiteId(command.jobSiteId())
                .startDate(command.startDate())
                .expectedEndDate(command.expectedEndDate())
                .startEngineHours(equipment.getCurrentEngineHours())
                .requiresHeavyTransport(command.requiresHeavyTransport())
                .status(initialStatus)
                .build();

        DispatchAllocation savedAllocation = dispatchRepo.save(allocation);

        meterRegistry.counter("titan.dispatch.created").increment();
        return savedAllocation;
    }

    @Transactional
    public void activateDispatch(UUID dispatchId) {
        DispatchAllocation dispatch = dispatchRepo.findById(dispatchId).orElseThrow();

        // ADDED: Allow AT_RISK jobs to be forced into ACTIVE if the dispatcher overrides
        if (dispatch.getStatus() != DispatchStatus.SCHEDULED && dispatch.getStatus() != DispatchStatus.PENDING && dispatch.getStatus() != DispatchStatus.AT_RISK) {
            throw new IllegalStateException("Only scheduled, pending, or at-risk dispatches can be activated.");
        }

        dispatch.setStatus(DispatchStatus.ACTIVE);

        // THE FIX: Now that the job is starting, physically lock the equipment
        Equipment equipment = dispatch.getEquipment();
        equipment.setStatus(EquipmentStatus.DISPATCHED);
        equipmentRepo.save(equipment);

        dispatchRepo.save(dispatch);
        log.info("Dispatch {} manually activated.", dispatchId);
    }

    @Transactional
    public void cancelDispatch(UUID dispatchId) {
        DispatchAllocation dispatch = dispatchRepo.findById(dispatchId).orElseThrow();

        if (dispatch.getStatus() == DispatchStatus.COMPLETED || dispatch.getStatus() == DispatchStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel a dispatch that is already completed or cancelled.");
        }

        dispatch.setStatus(DispatchStatus.CANCELLED);
        dispatch.setEndDate(LocalDateTime.now());

        Equipment equipment = dispatch.getEquipment();
        equipment.setStatus(EquipmentStatus.AVAILABLE);

        equipmentRepo.save(equipment);
        dispatchRepo.save(dispatch);

        meterRegistry.counter("titan.dispatch.cancelled").increment();
        log.info("Dispatch {} was cancelled. Equipment {} released.", dispatchId, equipment.getAssetTag());
    }

    @Transactional
    public void completeDispatch(UUID dispatchId, CompleteDispatchCommand command) {
        DispatchAllocation dispatch = dispatchRepo.findById(dispatchId).orElseThrow();

        if (dispatch.getStatus() != DispatchStatus.ACTIVE) {
            throw new IllegalStateException("Only active dispatches can be completed.");
        }

        Equipment equipment = dispatch.getEquipment();
        JobSite jobSite = jobSiteRepo.findById(dispatch.getJobSiteId()).orElseThrow();

        BigDecimal startHours = dispatch.getStartEngineHours();

        if (command.endHours().compareTo(startHours) < 0) {
            throw new IllegalArgumentException(String.format(
                    "End hours (%s) cannot be less than the start hours baseline (%s).",
                    command.endHours(), startHours
            ));
        }

        BigDecimal hoursUsed = command.endHours().subtract(startHours);
        BigDecimal totalJobCost = hoursUsed.multiply(equipment.getInternalHourlyRate());

        jobSite.setAccumulatedCost(jobSite.getAccumulatedCost().add(totalJobCost));

        if (equipment.getCurrentEngineHours() == null || command.endHours().compareTo(equipment.getCurrentEngineHours()) > 0) {
            equipment.setCurrentEngineHours(command.endHours());
        }

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

            meterRegistry.counter("titan.dispatch.completed").increment();
            meterRegistry.summary("titan.dispatch.revenue").record(totalJobCost.doubleValue());

            log.info("Dispatch {} completed. Cost: {}. Outbox event queued.", dispatchId, totalJobCost);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize dispatch completion event", e);
            throw new RuntimeException("System error securing dispatch completion logic");
        }
    }
}