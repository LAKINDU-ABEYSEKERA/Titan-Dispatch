package com.titan.dispatch.domain.enums;

public enum DispatchStatus {
    PENDING,   // Created, but equipment hasn't moved yet
    ACTIVE,    // Equipment is currently on the job site
    COMPLETED, // Job is done, ready for dynamic costing
    CANCELLED, // Dispatch was revoked before or during execution
    SCHEDULED, // Job is scheduled for the future
    AT_RISK    // NEW: Vehicle is locked in maintenance overlapping this job's start date
}