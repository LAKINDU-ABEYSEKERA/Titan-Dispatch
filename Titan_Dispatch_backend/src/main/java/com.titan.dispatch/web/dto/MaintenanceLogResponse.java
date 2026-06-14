package com.titan.dispatch.web.dto;

import com.titan.dispatch.domain.enums.ServiceType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceLogResponse(
        UUID id,
        UUID equipmentId,
        LocalDateTime serviceDate,
        BigDecimal hoursAtService,
        ServiceType serviceType,
        BigDecimal totalCost,
        String notes
) {}