package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record SendToShopCommand(
        @NotNull(message = "You must provide an estimated completion date")
        @Future(message = "Completion date must be in the future")
        LocalDateTime expectedEndDate
) {}