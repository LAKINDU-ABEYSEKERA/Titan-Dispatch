package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateFuelLogCommand(
        @NotNull UUID equipmentId,
        @NotNull UUID operatorId,
        @NotNull BigDecimal gallonsAdded,
        @NotNull BigDecimal totalCost,
        @NotNull BigDecimal engineHoursAtFillUp,
        @NotNull LocalDate fillDate
) {}