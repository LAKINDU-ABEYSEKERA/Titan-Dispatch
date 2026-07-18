package com.titan.dispatch.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActiveMaintenanceResponse(
        UUID equipmentId,
        String assetTag,
        LocalDateTime expectedEndDate
) {}