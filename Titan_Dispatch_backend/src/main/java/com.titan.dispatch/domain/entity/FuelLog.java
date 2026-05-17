package com.titan.dispatch.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fuel_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class FuelLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(name = "gallons_added", precision = 8, scale = 2, nullable = false)
    private BigDecimal gallonsAdded;

    @Column(name = "total_cost", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalCost;

    @Column(name = "engine_hours_at_fill_up", precision = 10, scale = 2, nullable = false)
    private BigDecimal engineHoursAtFillUp;
}