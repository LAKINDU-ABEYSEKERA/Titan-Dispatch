package com.titan.dispatch.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record UnauthorizedMovementAlertEvent(
        UUID equipmentId,
        UUID jobSiteId,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant timestamp
) {}