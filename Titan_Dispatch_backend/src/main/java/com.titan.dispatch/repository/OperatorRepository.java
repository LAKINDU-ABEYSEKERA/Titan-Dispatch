package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.Operator;
import com.titan.dispatch.domain.enums.OperatorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OperatorRepository extends JpaRepository<Operator, UUID> {
    List<Operator> findByStatus(OperatorStatus status);
}