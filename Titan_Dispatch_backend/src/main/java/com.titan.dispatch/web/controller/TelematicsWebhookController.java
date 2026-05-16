package com.titan.dispatch.web.controller;

import com.titan.dispatch.domain.event.TelematicsReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/webhooks/telematics")
@RequiredArgsConstructor
public class TelematicsWebhookController {

    private final ApplicationEventPublisher eventPublisher;
    private final StringRedisTemplate redisTemplate; // Requires spring-boot-starter-data-redis

    @PostMapping
    public ResponseEntity<String> receivePing(@RequestBody TelematicsReceivedEvent payload) {
        // Idempotency Check using Redis SETNX (set if absent)
        String redisKey = "telematics:processed:" + payload.messageId();
        Boolean isNewMessage = redisTemplate.opsForValue().setIfAbsent(redisKey, "true", Duration.ofHours(24));

        if (Boolean.TRUE.equals(isNewMessage)) {
            // It's a new message, publish it to the internal event bus
            eventPublisher.publishEvent(payload);
        } else {
            // Duplicate message detected, safely ignore but acknowledge receipt
        }

        return ResponseEntity.ok("ACK");
    }
}