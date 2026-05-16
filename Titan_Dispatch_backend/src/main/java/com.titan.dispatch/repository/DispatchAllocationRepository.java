package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.DispatchAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface DispatchAllocationRepository extends JpaRepository<DispatchAllocation, UUID> {
}