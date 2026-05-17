package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CompleteDispatchCommand(
        @NotNull(message = "End hours cannot be null")
        @DecimalMin(value = "0.0", inclusive = false, message = "End hours must be greater than zero")
        BigDecimal endHours
) {}