package com.titan.dispatch.service.event;

import com.titan.dispatch.domain.entity.OutboxEvent;
import com.titan.dispatch.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayWorker {

    private final OutboxEventRepository outboxRepo;

    // Runs every 10 seconds.
    @Scheduled(fixedDelayString = "${titan.outbox.poll-interval:10000}")
    @Transactional
    public void processOutboxEvents() {
        // The SKIP LOCKED query ensures we only grab free rows, ignoring ones locked by other threads
        List<OutboxEvent> events = outboxRepo.findUnprocessedEvents();

        if (events.isEmpty()) {
            return; // Sleep silently if there is no work to do
        }

        log.info("Outbox Relay woke up. Found {} unprocessed events.", events.size());

        for (OutboxEvent event : events) {
            try {
                // In a true microservice environment, you would push to AWS SQS or Kafka here.
                // For now, we simulate the successful push of the billing/completion event.
                log.info("Successfully published event '{}' for aggregate '{}'. Payload: {}",
                        event.getEventType(), event.getAggregateId(), event.getPayload());

                // Mark as processed ONLY if the simulated push succeeds
                event.setProcessed(true);

            } catch (Exception e) {
                log.error("Failed to process Outbox Event ID: {}", event.getId(), e);
                // We leave processed = false. It will automatically be retried on the next 10-second poll!
            }
        }

        // Save the updated states back to the database
        outboxRepo.saveAll(events);
    }
}