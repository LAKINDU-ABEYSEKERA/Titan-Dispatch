-- 1. Add the column to the main table (allow null temporarily to not break existing rows)
ALTER TABLE dispatch_allocation ADD COLUMN start_engine_hours NUMERIC(19, 2);

-- 2. Update any existing dispatches to have a baseline of 0 so we don't violate the NOT NULL constraint
UPDATE dispatch_allocation SET start_engine_hours = 0.0 WHERE start_engine_hours IS NULL;

-- 3. Lock down the column to NOT NULL to match your Java entity (@Column(nullable = false))
ALTER TABLE dispatch_allocation ALTER COLUMN start_engine_hours SET NOT NULL;

-- 4. Add the column to the Envers audit table (Audit tables do not need NOT NULL constraints)
ALTER TABLE dispatch_allocation_aud ADD COLUMN start_engine_hours NUMERIC(19, 2);