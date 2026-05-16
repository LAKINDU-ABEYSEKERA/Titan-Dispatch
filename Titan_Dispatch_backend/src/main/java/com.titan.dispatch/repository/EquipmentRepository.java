package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
}