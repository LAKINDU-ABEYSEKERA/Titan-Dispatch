package com.titan.dispatch.web.dto;

import com.titan.dispatch.domain.enums.ServiceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMaintenanceLogCommand(
        @NotNull(message = "Equipment ID is required") UUID equipmentId,
        @NotNull(message = "Service date is required") LocalDateTime serviceDate,
        @NotNull(message = "Hours at service required") @DecimalMin("0.0") BigDecimal hoursAtService,
        @NotNull(message = "Service type is required") ServiceType serviceType,
        @NotNull(message = "Total cost is required") @DecimalMin("0.0") BigDecimal totalCost,
        String notes
) {}