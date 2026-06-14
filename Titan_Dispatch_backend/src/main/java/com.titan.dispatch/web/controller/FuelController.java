package com.titan.dispatch.web.controller;

import com.titan.dispatch.service.FuelService;
import com.titan.dispatch.repository.FuelLogRepository;
import com.titan.dispatch.web.dto.CreateFuelLogCommand;
import com.titan.dispatch.web.dto.FuelLogResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fuel")
@RequiredArgsConstructor
public class FuelController {

    private final FuelService fuelService;
    private final FuelLogRepository fuelLogRepo;

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @PostMapping
    public ResponseEntity<Void> submitLog(@Valid @RequestBody CreateFuelLogCommand request) {
        fuelService.submitFuelLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIC', 'DISPATCH')")
    @GetMapping("/{equipmentId}")
    public ResponseEntity<List<FuelLogResponse>> getHistory(@PathVariable UUID equipmentId) {
        List<FuelLogResponse> response = fuelLogRepo.findByEquipmentIdOrderByFillDateDesc(equipmentId)
                .stream()
                .map(l -> new FuelLogResponse(
                        l.getId(),
                        l.getEquipment().getId(),
                        l.getOperator().getId(),
                        l.getGallonsAdded(),
                        l.getTotalCost(),
                        l.getEngineHoursAtFillUp(),
                        l.getFillDate()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}