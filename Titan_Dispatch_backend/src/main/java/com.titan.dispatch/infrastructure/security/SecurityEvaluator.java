package com.titan.dispatch.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component("securityEvaluator")
public class SecurityEvaluator {

    /**
     * Evaluates if the authenticated user has permission to allocate to this specific job site.
     * In a real scenario, this might query the database to check user-region assignments.
     */
    public boolean canManageJobSite(Authentication authentication, UUID jobSiteId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // Extract user ID or details from the custom JWT Authentication token
        String username = authentication.getName();

        // Example Enterprise Logic:
        // boolean hasAccess = userRegionRepository.existsByUsernameAndJobSiteId(username, jobSiteId);
        // return hasAccess;

        // Placeholder logic to allow the compile/run for now
        return jobSiteId != null;
    }
}