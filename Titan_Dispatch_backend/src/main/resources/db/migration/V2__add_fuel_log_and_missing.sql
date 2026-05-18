CREATE TABLE fuel_log (
    id UUID PRIMARY KEY,
    equipment_id UUID NOT NULL REFERENCES equipment(id),
    operator_id UUID NOT NULL REFERENCES operator(id),
    gallons_added DECIMAL(8,2) NOT NULL,
    total_cost DECIMAL(10,2) NOT NULL,
    engine_hours_at_fill_up DECIMAL(10,2) NOT NULL,
    created_by UUID,
    created_at TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE fuel_log_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    equipment_id UUID,
    operator_id UUID,
    gallons_added DECIMAL(8,2),
    total_cost DECIMAL(10,2),
    engine_hours_at_fill_up DECIMAL(10,2),
    deleted BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE INDEX idx_fuel_equipment ON fuel_log(equipment_id);
CREATE INDEX idx_maintenance_equipment ON maintenance_log(equipment_id);