package com.titan.dispatch.service;

import com.titan.dispatch.domain.entity.JobSite;
import com.titan.dispatch.repository.JobSiteRepository;
import com.titan.dispatch.web.dto.JobSiteCommands.CreateJobSiteCommand;
import com.titan.dispatch.web.dto.JobSiteCommands.UpdateJobSiteCommand;
import com.titan.dispatch.web.dto.JobSiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobSiteAdminService {

    private final JobSiteRepository jobSiteRepo;

    @Transactional(readOnly = true)
    public List<JobSiteResponse> getAllJobSites() {
        return jobSiteRepo.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public JobSiteResponse createJobSite(CreateJobSiteCommand cmd) {
        if (jobSiteRepo.existsByProjectCode(cmd.projectCode())) {
            throw new IllegalArgumentException("Project code already exists in the system.");
        }

        JobSite jobSite = JobSite.builder()
                .projectCode(cmd.projectCode())
                .siteName(cmd.siteName())
                .latitude(cmd.latitude())
                .longitude(cmd.longitude())
                .geofenceRadiusMeters(cmd.geofenceRadiusMeters())
                .accumulatedCost(BigDecimal.ZERO)
                .build();

        return mapToResponse(jobSiteRepo.save(jobSite));
    }

    @Transactional
    public JobSiteResponse updateJobSite(UUID id, UpdateJobSiteCommand cmd) {
        JobSite jobSite = jobSiteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job site not found."));

        jobSite.setSiteName(cmd.siteName());
        jobSite.setLatitude(cmd.latitude());
        jobSite.setLongitude(cmd.longitude());
        jobSite.setGeofenceRadiusMeters(cmd.geofenceRadiusMeters());

        return mapToResponse(jobSiteRepo.save(jobSite));
    }

    @Transactional
    public void deleteJobSite(UUID id) {
        JobSite jobSite = jobSiteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job site not found."));
        jobSiteRepo.delete(jobSite);
    }

    private JobSiteResponse mapToResponse(JobSite j) {
        return new JobSiteResponse(
                j.getId(),
                j.getProjectCode(),
                j.getSiteName(),
                j.getLatitude(),
                j.getLongitude(),
                j.getGeofenceRadiusMeters(),
                j.getAccumulatedCost()
        );
    }
}