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

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH') and @securityEvaluator.canManageJobSite(authentication, #request.jobSiteId())")
    @PostMapping("/allocate")
    public ResponseEntity<Void> allocate(@Valid @RequestBody CreateDispatchCommand request) {
        dispatchService.createDispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateDispatch(@PathVariable("id") UUID id) {
        dispatchService.activateDispatch(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeDispatch(@PathVariable("id") UUID id, @Valid @RequestBody CompleteDispatchCommand request) {
        dispatchService.completeDispatch(id, request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelDispatch(@PathVariable("id") UUID id) {
        dispatchService.cancelDispatch(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<DispatchSummaryResponse>> getDispatches(@RequestParam(value = "status", required = false) DispatchStatus status) {
        return ResponseEntity.ok(queryService.getDispatches(status));
    }
}