package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OperatorRepository extends JpaRepository<Operator, UUID> {
}