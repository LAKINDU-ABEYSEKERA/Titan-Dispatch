package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, UUID> {
}