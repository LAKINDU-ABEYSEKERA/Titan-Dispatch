package com.titan.dispatch.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceLogResponse(
        UUID id,
        UUID equipmentId,
        String assetTag, // UPGRADED: Human-readable identifier
        LocalDateTime serviceDate,
        BigDecimal hoursAtService,
        String serviceType,
        BigDecimal totalCost,
        String notes
) {}