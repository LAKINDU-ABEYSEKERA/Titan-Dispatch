package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FuelLogRepository extends JpaRepository<FuelLog, UUID> {
}