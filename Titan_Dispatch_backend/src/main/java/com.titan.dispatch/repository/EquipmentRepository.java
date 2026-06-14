package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.Equipment;
import com.titan.dispatch.domain.enums.EquipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    List<Equipment> findByStatus(EquipmentStatus status);

}