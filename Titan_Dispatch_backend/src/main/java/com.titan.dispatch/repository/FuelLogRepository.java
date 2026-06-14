package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.FuelLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FuelLogRepository extends JpaRepository<FuelLog, UUID> {
    // Spring Data JPA will now successfully find 'fillDate' and write the SQL!
    List<FuelLog> findByEquipmentIdOrderByFillDateDesc(UUID equipmentId);
}