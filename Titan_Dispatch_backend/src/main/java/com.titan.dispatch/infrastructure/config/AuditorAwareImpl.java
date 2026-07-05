package com.titan.dispatch.infrastructure.config;

import com.titan.dispatch.infrastructure.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("auditorAware")
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        // FIX: Safely extract the UUID from our CustomUserDetails wrapper
        if (authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            return Optional.of(customUserDetails.getId());
        }

        return Optional.empty();
    }
}