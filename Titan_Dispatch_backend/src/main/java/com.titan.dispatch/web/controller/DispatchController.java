package com.titan.dispatch.web.controller;

import com.titan.dispatch.web.dto.CreateDispatchCommand;
import com.titan.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}