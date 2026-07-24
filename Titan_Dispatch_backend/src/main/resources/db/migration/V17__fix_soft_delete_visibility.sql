UPDATE equipment SET deleted = false WHERE deleted IS NULL;
UPDATE equipment_aud SET deleted = false WHERE deleted IS NULL;
UPDATE maintenance_log SET deleted = false WHERE deleted IS NULL;
UPDATE dispatch_allocation SET deleted = false WHERE deleted IS NULL;
UPDATE job_site SET deleted = false WHERE deleted IS NULL;
UPDATE operator SET deleted = false WHERE deleted IS NULL;