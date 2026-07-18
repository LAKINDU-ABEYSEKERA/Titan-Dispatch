package com.titan.dispatch.repository;

import com.titan.dispatch.domain.entity.JobSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface JobSiteRepository extends JpaRepository<JobSite, UUID> {

    // Utilizes PostGIS ST_DWithin casting coordinates to geography for accurate meter-based radius checking
    @Query(value = """
        SELECT ST_DWithin(
            CAST(ST_SetSRID(ST_MakePoint(:lon, :lat), 4326) AS geography),
            CAST(ST_SetSRID(ST_MakePoint(j.longitude, j.latitude), 4326) AS geography),
            j.geofence_radius_meters
        ) 
        FROM job_site j WHERE j.id = :jobSiteId
    """, nativeQuery = true)
    Boolean isWithinGeofence(@Param("jobSiteId") UUID jobSiteId, @Param("lat") BigDecimal lat, @Param("lon") BigDecimal lon);

    // NEW: Required for the Job Site Analytics creation logic
    boolean existsByProjectCode(String projectCode);
}