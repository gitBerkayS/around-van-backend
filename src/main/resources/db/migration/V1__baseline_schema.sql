CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS neighbourhood (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    boundary geometry(Polygon, 4326) NOT NULL,
    municipality VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS location (
    id BIGSERIAL PRIMARY KEY,
    neighbourhood_id BIGINT REFERENCES neighbourhood(id),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    postal_code_prefix VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    home_location_id BIGINT REFERENCES location(id)
);

CREATE TABLE IF NOT EXISTS event (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    published_date TIMESTAMPTZ,
    date_end TIMESTAMPTZ,
    date_start TIMESTAMPTZ,
    location_id BIGINT REFERENCES location(id),
    provider VARCHAR(50),
    external_id VARCHAR(255),
    external_url TEXT,
    image_url TEXT,
    CONSTRAINT uk_event_provider_external_id UNIQUE (provider, external_id)
);
