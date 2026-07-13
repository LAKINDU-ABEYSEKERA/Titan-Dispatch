package com.titan.dispatch.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public record OperatorResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate licenseExpiration
) {}