package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EquipmentCommands {

    public record CreateEquipmentCommand(
            @NotBlank(message = "Asset tag is required") String assetTag,
            @NotNull(message = "Hourly rate is required") @DecimalMin("0.01") BigDecimal internalHourlyRate,
            @NotNull(message = "Insurance expiration is required") LocalDate insuranceExpiration
    ) {}

    public record UpdateEquipmentCommand(
            @NotBlank(message = "Asset tag is required") String assetTag,
            @NotNull(message = "Hourly rate is required") @DecimalMin("0.01") BigDecimal internalHourlyRate,
            @NotNull(message = "Insurance expiration is required") LocalDate insuranceExpiration
    ) {}
}