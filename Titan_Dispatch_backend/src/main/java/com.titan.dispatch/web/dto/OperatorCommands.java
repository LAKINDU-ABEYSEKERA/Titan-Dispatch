package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class OperatorCommands {
    public record CreateOperatorCommand(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotNull(message = "License expiration is required")
            @Future(message = "Cannot register an operator with an already expired license") LocalDate licenseExpiration
    ) {}

    public record UpdateOperatorCommand(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotNull(message = "License expiration is required") LocalDate licenseExpiration
    ) {}
}