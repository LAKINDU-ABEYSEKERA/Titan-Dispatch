package com.titan.dispatch.domain.entity;

import com.titan.dispatch.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "system_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemUser extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}