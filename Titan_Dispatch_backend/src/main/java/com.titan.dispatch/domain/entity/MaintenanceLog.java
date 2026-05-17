package com.titan.dispatch.domain.entity;

import com.titan.dispatch.domain.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class MaintenanceLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "service_date", nullable = false)
    private LocalDateTime serviceDate;

    @Column(name = "hours_at_service", precision = 10, scale = 2, nullable = false)
    private BigDecimal hoursAtService;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false)
    private ServiceType serviceType;

    @Column(name = "total_cost", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalCost;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}