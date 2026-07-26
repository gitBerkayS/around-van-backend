CREATE TABLE IF NOT EXISTS wildfire (
    id BIGSERIAL PRIMARY KEY,
    fire_number VARCHAR(255) NOT NULL UNIQUE,
    bc_fire_id INTEGER,
    incident_name VARCHAR(255),
    geographic_description VARCHAR(2000),
    location_id BIGINT UNIQUE REFERENCES location(id),
    current_size_hectares DOUBLE PRECISION,
    status VARCHAR(255),
    cause VARCHAR(255),
    response_type VARCHAR(255),
    fire_centre_code INTEGER,
    zone_code INTEGER,
    fire_type VARCHAR(255),
    ignition_date TIMESTAMPTZ,
    fire_out_date TIMESTAMPTZ,
    fire_of_note BOOLEAN NOT NULL DEFAULT FALSE,
    was_fire_of_note BOOLEAN NOT NULL DEFAULT FALSE,
    fire_url TEXT,
    last_synced_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_wildfire_status ON wildfire (status);
