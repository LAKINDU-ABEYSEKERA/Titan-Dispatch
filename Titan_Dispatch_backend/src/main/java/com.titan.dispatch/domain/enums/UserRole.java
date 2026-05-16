package com.titan.dispatch.domain.enums;

public enum UserRole {
    ADMIN,
    DISPATCH,
    MECHANIC;

    // Standardized for Spring Security Authority mapping
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}