package com.titan.dispatch.web.dto;

import java.util.UUID;

public record JobSiteDropdown(
        UUID id,
        String projectCode
) {}