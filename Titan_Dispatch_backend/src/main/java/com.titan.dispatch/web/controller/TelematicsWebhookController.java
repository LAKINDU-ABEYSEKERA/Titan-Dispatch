package com.titan.dispatch.web.controller;

import com.titan.dispatch.domain.event.TelematicsReceivedEvent;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/telematics")
@RequiredArgsConstructor
public class TelematicsWebhookController {

    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate;

    // In-memory cache of buckets per equipment ID
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<String> receivePing(@RequestBody TelematicsReceivedEvent payload) {

        // 1. RATE LIMITING SHIELD (Bucket4j)
        // Ensure no single bulldozer can spam the API and crash the server
        Bucket bucket = resolveBucket(payload.equipmentId().toString()); // Assumes equipmentId is UUID/String

        if (!bucket.tryConsume(1)) {
            log.warn("RATE LIMIT EXCEEDED for Equipment: {}", payload.equipmentId());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("429 Too Many Requests");
        }

        // 2. IDEMPOTENCY CHECK (Redis)
        // Prevent accidental duplicate pings from being processed twice
        String redisKey = "telematics:processed:" + payload.messageId();
        Boolean isNewMessage = redisTemplate.opsForValue().setIfAbsent(redisKey, "true", Duration.ofHours(24));

        if (Boolean.TRUE.equals(isNewMessage)) {
            // 3. PROCESS VALID PING
            eventPublisher.publishEvent(payload);
        } else {
            log.debug("Duplicate ping detected and ignored: {}", payload.messageId());
        }

        return ResponseEntity.ok("ACK");
    }

    // Assigns a strict limit of 5 pings per minute per equipment
    private Bucket resolveBucket(String equipmentId) {
        return cache.computeIfAbsent(equipmentId, this::newBucket);
    }

    private Bucket newBucket(String equipmentId) {
        // Modern Bucket4j Builder Syntax (Replaces deprecated Bandwidth.classic)
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillIntervally(5, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}