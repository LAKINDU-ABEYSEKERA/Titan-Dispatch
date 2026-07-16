package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;
import java.time.LocalDateTime;

public record CreateDispatchCommand(
        @NotNull(message = "Equipment ID is required")
        UUID equipmentId,

        @NotNull(message = "Operator ID is required")
        UUID operatorId,

        @NotNull(message = "Job Site ID is required")
        UUID jobSiteId,

        @FutureOrPresent(message = "Start date cannot be in the past")
        @NotNull(message = "Start date is required")
        LocalDateTime startDate,

        @Future(message = "Expected end date must be in the future")
        @NotNull(message = "Expected end date is required to secure the scheduling window")
        LocalDateTime expectedEndDate,

        boolean requiresHeavyTransport
) {}