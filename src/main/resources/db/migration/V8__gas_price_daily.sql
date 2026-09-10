CREATE TABLE IF NOT EXISTS gas_price_daily (
    id BIGSERIAL PRIMARY KEY,
    station_id BIGINT NOT NULL REFERENCES gas_station(id),
    fuel_type VARCHAR(20) NOT NULL,
    price_date DATE NOT NULL,
    price NUMERIC(6, 3) NOT NULL,
    observed_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_gas_price_daily_station_fuel_date UNIQUE (station_id, fuel_type, price_date)
);

CREATE INDEX IF NOT EXISTS idx_gas_price_daily_fuel_date
    ON gas_price_daily (fuel_type, price_date);
