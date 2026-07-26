package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.repository.OperatorRepository;
import com.titan.dispatch.web.dto.OperatorCommands.CreateOperatorCommand;
import com.titan.dispatch.web.dto.OperatorCommands.UpdateOperatorCommand;
import com.titan.dispatch.web.dto.OperatorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorAdminService {

    private final OperatorRepository operatorRepo;

    @Transactional(readOnly = true)
    public List<OperatorResponse> getAllOperators() {
        return operatorRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OperatorResponse createOperator(CreateOperatorCommand cmd) {
        Operator operator = Operator.builder()
                .firstName(cmd.firstName())
                .lastName(cmd.lastName())
                .licenseExpiration(cmd.licenseExpiration())
                .hourlyRate(cmd.hourlyRate())
                .build();

        return mapToResponse(operatorRepo.save(operator));
    }

    @Transactional
    public OperatorResponse updateOperator(UUID id, UpdateOperatorCommand cmd) {
        Operator operator = operatorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operator not found"));

        operator.setFirstName(cmd.firstName());
        operator.setLastName(cmd.lastName());
        operator.setLicenseExpiration(cmd.licenseExpiration());

        return mapToResponse(operatorRepo.save(operator));
    }

    @Transactional
    public void deleteOperator(UUID id) {
        Operator operator = operatorRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Operator not found"));

        // Triggers Hibernate @SoftDelete (deleted = true)
        operatorRepo.delete(operator);
    }

    private OperatorResponse mapToResponse(Operator o) {
        return new OperatorResponse(
                o.getId(),
                o.getFirstName(),
                o.getLastName(),
                o.getLicenseExpiration()
        );
    }
}