CREATE TABLE IF NOT EXISTS aqhi_region (
    location_id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS aqhi_reading (
    id BIGSERIAL PRIMARY KEY,
    region_location_id VARCHAR(255) NOT NULL REFERENCES aqhi_region(location_id),
    value DOUBLE PRECISION NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    fetched_at TIMESTAMPTZ,
    CONSTRAINT uk_aqhi_reading_region_observed_at UNIQUE (region_location_id, observed_at)
);

CREATE INDEX IF NOT EXISTS idx_aqhi_reading_region_observed_at
    ON aqhi_reading (region_location_id, observed_at DESC);

ALTER TABLE neighbourhood
    ADD COLUMN IF NOT EXISTS aqhi_region_location_id VARCHAR(255)
        REFERENCES aqhi_region(location_id);
