package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.JobSiteAdminService;
import com.titan.dispatch.web.dto.JobSiteCommands.CreateJobSiteCommand;
import com.titan.dispatch.web.dto.JobSiteCommands.UpdateJobSiteCommand;
import com.titan.dispatch.web.dto.JobSiteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job-sites")
@RequiredArgsConstructor
public class JobSiteController {

    private final JobSiteAdminService jobSiteService;

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<JobSiteResponse>> getAllJobSites() {
        return ResponseEntity.ok(jobSiteService.getAllJobSites());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<JobSiteResponse> createJobSite(@Valid @RequestBody CreateJobSiteCommand cmd) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobSiteService.createJobSite(cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<JobSiteResponse> updateJobSite(@PathVariable UUID id, @Valid @RequestBody UpdateJobSiteCommand cmd) {
        return ResponseEntity.ok(jobSiteService.updateJobSite(id, cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobSite(@PathVariable UUID id) {
        jobSiteService.deleteJobSite(id);
        return ResponseEntity.noContent().build();
    }
}