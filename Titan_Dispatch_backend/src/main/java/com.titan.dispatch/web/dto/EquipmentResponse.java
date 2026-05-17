package com.titan.dispatch.web.dto;

import com.titan.dispatch.domain.enums.EquipmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String assetTag,
        EquipmentStatus status,
        BigDecimal currentEngineHours,
        BigDecimal internalHourlyRate,
        LocalDate insuranceExpiration
) {}