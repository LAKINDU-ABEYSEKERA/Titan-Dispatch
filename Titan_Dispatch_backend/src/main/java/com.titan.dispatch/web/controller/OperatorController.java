package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.OperatorAdminService;
import com.titan.dispatch.web.dto.OperatorCommands.CreateOperatorCommand;
import com.titan.dispatch.web.dto.OperatorCommands.UpdateOperatorCommand;
import com.titan.dispatch.web.dto.OperatorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operators")
@RequiredArgsConstructor
public class OperatorController {

    private final OperatorAdminService operatorService;

    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCH')")
    @GetMapping
    public ResponseEntity<List<OperatorResponse>> getAllOperators() {
        return ResponseEntity.ok(operatorService.getAllOperators());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<OperatorResponse> createOperator(@Valid @RequestBody CreateOperatorCommand cmd) {
        return ResponseEntity.status(HttpStatus.CREATED).body(operatorService.createOperator(cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OperatorResponse> updateOperator(@PathVariable UUID id, @Valid @RequestBody UpdateOperatorCommand cmd) {
        return ResponseEntity.ok(operatorService.updateOperator(id, cmd));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperator(@PathVariable UUID id) {
        operatorService.deleteOperator(id);
        return ResponseEntity.noContent().build();
    }
}