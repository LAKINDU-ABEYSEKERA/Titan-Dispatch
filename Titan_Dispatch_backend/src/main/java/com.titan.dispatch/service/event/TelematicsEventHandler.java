package com.titan.dispatch.service.event;

import com.titan.dispatch.domain.event.TelematicsReceivedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TelematicsEventHandler {

    @Async("telematicsExecutor")
    @EventListener
    public void handleTelematicsPing(TelematicsReceivedEvent event) {
        log.info("Processing telematics event async: {}", event.messageId());

        // 1. PostGIS query to verify if coordinates are within job site radius
        // 2. Update equipment current_engine_hours
        // 3. Update accumulated_cost on Job_Site
    }
}