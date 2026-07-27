CREATE TABLE IF NOT EXISTS gas_station (
    id BIGSERIAL PRIMARY KEY,
    external_key VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(1000) NOT NULL,
    postal_code_prefix VARCHAR(10) NOT NULL,
    location_id BIGINT UNIQUE REFERENCES location(id),
    last_synced_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS gas_price (
    id BIGSERIAL PRIMARY KEY,
    station_id BIGINT NOT NULL REFERENCES gas_station(id),
    fuel_type VARCHAR(20) NOT NULL,
    price NUMERIC(6, 3) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_gas_price_station_fuel UNIQUE (station_id, fuel_type)
);

CREATE INDEX IF NOT EXISTS idx_gas_station_postal ON gas_station (postal_code_prefix);
CREATE INDEX IF NOT EXISTS idx_gas_price_fuel ON gas_price (fuel_type);

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS preferred_fuel_type VARCHAR(20) NOT NULL DEFAULT 'REGULAR';
