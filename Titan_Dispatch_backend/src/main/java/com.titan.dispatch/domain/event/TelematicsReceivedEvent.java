package com.titan.dispatch.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TelematicsReceivedEvent(
        String messageId,
        UUID equipmentId,
        BigDecimal latitude,
        BigDecimal longitude,
        BigDecimal engineHours,
        Instant timestamp
) {}