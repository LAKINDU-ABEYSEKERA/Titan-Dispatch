package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.DispatchService;
import com.titan.dispatch.web.dto.CompleteDispatchCommand;
import com.titan.dispatch.web.dto.CreateDispatchCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @PreAuthorize("hasRole('DISPATCH') and @securityEvaluator.canManageJobSite(authentication, #request.jobSiteId())")
    @PostMapping("/allocate")
    public ResponseEntity<Void> allocate(@Valid @RequestBody CreateDispatchCommand request) {
        dispatchService.createDispatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('DISPATCH')")
    @PutMapping("/{id}/complete")
    public ResponseEntity<Void> completeDispatch(@PathVariable UUID id, @Valid @RequestBody CompleteDispatchCommand request) {
        dispatchService.completeDispatch(id, request);
        return ResponseEntity.ok().build();
    }
}