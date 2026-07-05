package com.titan.dispatch.web.controller;

import com.titan.dispatch.domain.enums.DispatchStatus;
import com.titan.dispatch.service.DispatchService;
import com.titan.dispatch.service.QueryService;
import com.titan.dispatch.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;
    private final QueryService queryService;

    // FIX: Changed hasRole to hasAnyRole so it can accept multiple arguments
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH') and @securityEvaluator.canManageJobSite(authentication, #request.jobSiteId())")
    @PostMapping("/allocate")
    public ResponseEntity<Void> allocate(@Valid @RequestBody CreateDispatchCommand request) {
        dispatchService.createDispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeDispatch(@PathVariable UUID id, @Valid @RequestBody CompleteDispatchCommand request) {
        dispatchService.completeDispatch(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<DispatchSummaryResponse>> getDispatches(@RequestParam(required = false) DispatchStatus status) {
        return ResponseEntity.ok(queryService.getDispatches(status));
    }
}