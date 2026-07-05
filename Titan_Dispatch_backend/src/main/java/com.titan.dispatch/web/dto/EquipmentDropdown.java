package com.titan.dispatch.web.dto;

import java.util.UUID;

public record EquipmentDropdown(
        UUID id,
        String assetTag
) {}