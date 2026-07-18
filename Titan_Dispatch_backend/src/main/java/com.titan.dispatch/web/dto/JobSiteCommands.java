package com.titan.dispatch.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class JobSiteCommands {

    public record CreateJobSiteCommand(
            @NotBlank(message = "Project code is required") String projectCode,
            @NotBlank(message = "Site name is required") String siteName,
            @NotNull(message = "Latitude is required") BigDecimal latitude,
            @NotNull(message = "Longitude is required") BigDecimal longitude,
            @NotNull(message = "Geofence radius is required")
            @Positive(message = "Radius must be positive") Integer geofenceRadiusMeters
    ) {}

    public record UpdateJobSiteCommand(
            @NotBlank(message = "Site name is required") String siteName,
            @NotNull(message = "Latitude is required") BigDecimal latitude,
            @NotNull(message = "Longitude is required") BigDecimal longitude,
            @NotNull(message = "Geofence radius is required")
            @Positive(message = "Radius must be positive") Integer geofenceRadiusMeters
    ) {}
}