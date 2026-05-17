package com.titan.dispatch.service.event;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.entity.JobSite;
import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.domain.event.TelematicsReceivedEvent;
import com.titan.dispatch.domain.event.UnauthorizedMovementAlertEvent;
import com.titan.dispatch.repository.DispatchAllocationRepository;
import com.titan.dispatch.repository.EquipmentRepository;
import com.titan.dispatch.repository.JobSiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelematicsEventHandler {

    private final EquipmentRepository equipmentRepo;
    private final DispatchAllocationRepository dispatchRepo;
    private final JobSiteRepository jobSiteRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Async("telematicsExecutor")
    @EventListener
    @Transactional
    public void handleTelematicsPing(TelematicsReceivedEvent event) {
        log.info("Processing telematics ping for equipment: {}", event.equipmentId());

        Equipment equipment = equipmentRepo.findById(event.equipmentId()).orElseThrow();

        BigDecimal hoursDiff = event.engineHours().subtract(equipment.getCurrentEngineHours());
        if (hoursDiff.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal costIncrease = hoursDiff.multiply(equipment.getInternalHourlyRate());

            dispatchRepo.findByEquipmentIdAndStatus(equipment.getId(), DispatchStatus.ACTIVE).ifPresent(dispatch -> {
                JobSite site = jobSiteRepo.findById(dispatch.getJobSiteId()).orElseThrow();
                site.setAccumulatedCost(site.getAccumulatedCost().add(costIncrease));
                jobSiteRepo.save(site);

                Boolean isSafe = jobSiteRepo.isWithinGeofence(site.getId(), event.latitude(), event.longitude());
                if (Boolean.FALSE.equals(isSafe)) {
                    log.error("ALERT: Equipment {} breached geofence at JobSite {}", equipment.getAssetTag(), site.getProjectCode());
                    eventPublisher.publishEvent(new UnauthorizedMovementAlertEvent(
                            equipment.getId(),
                            site.getId(),
                            event.latitude(),
                            event.longitude(),
                            Instant.now()
                    ));
                }
            });
        }

        equipment.setCurrentEngineHours(event.engineHours());
        equipmentRepo.save(equipment);
    }
}