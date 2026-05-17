package com.titan.dispatch.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter dispatchCreationsCounter(MeterRegistry registry) {
        return Counter.builder("titan.dispatch.creations")
                .description("Total number of successful dispatch allocations")
                .register(registry);
    }

    @Bean
    public Counter dispatchCompletionsCounter(MeterRegistry registry) {
        return Counter.builder("titan.dispatch.completions")
                .description("Total number of completed dispatches")
                .register(registry);
    }

    @Bean
    public Counter geofenceBreachesCounter(MeterRegistry registry) {
        return Counter.builder("titan.geofence.breaches")
                .description("Total number of unauthorized geofence breaches detected")
                .register(registry);
    }

    @Bean
    public Counter telematicsDuplicateMessagesCounter(MeterRegistry registry) {
        return Counter.builder("titan.telematics.duplicate.messages")
                .description("Total number of duplicate IoT webhooks rejected by Redis idempotency")
                .register(registry);
    }
}