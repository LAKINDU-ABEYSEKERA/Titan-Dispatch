package com.titan.dispatch.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record JobSiteResponse(
        UUID id,
        String projectCode,
        String siteName,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer geofenceRadiusMeters,
        BigDecimal accumulatedCost
) {}