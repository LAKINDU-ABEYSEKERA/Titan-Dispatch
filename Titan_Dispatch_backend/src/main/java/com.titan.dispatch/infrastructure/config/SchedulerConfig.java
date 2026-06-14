package com.titan.dispatch.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // This simple class acts as the master switch to allow @Scheduled tasks to run in the background.
}