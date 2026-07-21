package com.titan.dispatch.domain.entity;

import com.titan.dispatch.domain.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Equipment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "asset_tag", nullable = false, unique = true)
    private String assetTag;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EquipmentStatus status;

    @Column(name = "current_engine_hours", precision = 10, scale = 2)
    private BigDecimal currentEngineHours;

    @Column(name = "internal_hourly_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal internalHourlyRate;

    @Column(name = "insurance_expiration", nullable = false)
    private LocalDate insuranceExpiration;

    @Column(name = "maintenance_start_date")
    private LocalDateTime maintenanceStartDate;

    @Column(name = "expected_maintenance_end_date")
    private LocalDateTime expectedMaintenanceEndDate;
}