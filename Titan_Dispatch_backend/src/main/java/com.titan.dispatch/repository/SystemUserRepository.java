package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.SystemUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SystemUserRepository extends JpaRepository<SystemUser, UUID> {
    Optional<SystemUser> findByUsername(String username);
}