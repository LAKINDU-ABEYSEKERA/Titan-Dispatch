package com.titan.dispatch.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FuelLogResponse(
        UUID id,
        UUID equipmentId,
        UUID operatorId,
        BigDecimal gallonsAdded,
        BigDecimal totalCost,
        BigDecimal engineHoursAtFillUp,
        LocalDate fillDate
) {}