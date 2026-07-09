package com.titan.dispatch.domain.entity;

import com.titan.dispatch.domain.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dispatch_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class DispatchAllocation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(name = "job_site_id", nullable = false)
    private UUID jobSiteId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "requires_heavy_transport", nullable = false)
    private Boolean requiresHeavyTransport;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DispatchStatus status;

    @Column(name = "start_engine_hours", nullable = false)
    private BigDecimal startEngineHours;
}