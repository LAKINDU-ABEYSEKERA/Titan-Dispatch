package com.titan.dispatch.web.dto;
import com.titan.dispatch.domain.enums.DispatchStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record DispatchSummaryResponse(
        UUID id,
        String equipmentTag,
        String operatorName,
        String projectCode,
        DispatchStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}