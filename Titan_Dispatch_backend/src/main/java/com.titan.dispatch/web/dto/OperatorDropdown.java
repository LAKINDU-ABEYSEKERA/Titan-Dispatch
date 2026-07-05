package com.titan.dispatch.web.dto;

import java.util.UUID;

public record OperatorDropdown(
        UUID id,
        String firstName,
        String lastName
) {}