-- Insert sample job sites matching the exact JobSite entity definition
INSERT INTO job_site (
    id,
    project_code,
    site_name,
    latitude,
    longitude,
    geofence_radius_meters,
    accumulated_cost
    -- Note: If your Auditable class enforces non-null fields (like created_date or created_by),
    -- add them here (e.g., created_date, created_by) and in the VALUES below (e.g., CURRENT_TIMESTAMP, 'system').
)
VALUES
    (gen_random_uuid(), 'PRJ-ALPHA-101', 'Downtown Excavation', 6.927079, 79.861244, 500, 0.00),
    (gen_random_uuid(), 'PRJ-OMEGA-202', 'Northern Highway Expansion', 6.936551, 79.845012, 1500, 0.00),
    (gen_random_uuid(), 'PRJ-DELTA-303', 'Riverside Grading', 6.899450, 79.858200, 250, 0.00);