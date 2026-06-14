package com.titan.dispatch.web.dto;
import com.titan.dispatch.domain.enums.ServiceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMaintenanceLogCommand(
        @NotNull UUID equipmentId,
        @NotNull LocalDateTime serviceDate,
        @NotNull BigDecimal hoursAtService,
        @NotNull ServiceType serviceType,
        @NotNull BigDecimal totalCost,
        String notes // Optional, so no @NotNull
) {}