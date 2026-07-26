package com.titan.dispatch.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "job_site")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // <-- UPGRADED: Allows inheritance from Auditable
@Audited
public class JobSite extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "project_code", nullable = false, unique = true)
    private String projectCode;

    @Column(name = "site_name", nullable = false)
    private String siteName;

    @Column(name = "latitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;

    @Column(name = "geofence_radius_meters", nullable = false)
    private Integer geofenceRadiusMeters;

    @Column(name = "accumulated_cost", precision = 12, scale = 2, nullable = false)
    private BigDecimal accumulatedCost;

    @Column(name = "heavy_transport_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal heavyTransportRate;
}