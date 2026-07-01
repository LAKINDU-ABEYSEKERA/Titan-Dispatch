package com.titan.dispatch.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails extends User {

    private final UUID id;

    public CustomUserDetails(
            UUID id,
            String username,
            String password,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(username, password, enabled, true, true, true, authorities);
        this.id = id;
    }

    /**
     * Safely fetch the database key without string conversions.
     */
    public UUID getId() {
        return this.id;
    }
}