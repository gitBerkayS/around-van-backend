CREATE TABLE IF NOT EXISTS service_request (
    id BIGSERIAL PRIMARY KEY,
    external_key VARCHAR(64) NOT NULL UNIQUE,
    request_type VARCHAR(255) NOT NULL,
    category VARCHAR(40) NOT NULL,
    importance VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    department VARCHAR(255),
    address VARCHAR(1000) NOT NULL,
    local_area VARCHAR(255),
    channel VARCHAR(100),
    opened_at TIMESTAMPTZ NOT NULL,
    last_modified_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ,
    last_synced_at TIMESTAMPTZ,
    location_id BIGINT UNIQUE REFERENCES location(id)
);

CREATE INDEX IF NOT EXISTS idx_service_request_status ON service_request (status);
CREATE INDEX IF NOT EXISTS idx_service_request_category ON service_request (category);
CREATE INDEX IF NOT EXISTS idx_service_request_importance ON service_request (importance);
CREATE INDEX IF NOT EXISTS idx_service_request_opened_at ON service_request (opened_at DESC);

CREATE TABLE IF NOT EXISTS user_service_request_seen (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    service_request_id BIGINT NOT NULL REFERENCES service_request(id) ON DELETE CASCADE,
    seen_at TIMESTAMPTZ NOT NULL,
    PRIMARY KEY (user_id, service_request_id)
);

CREATE TABLE IF NOT EXISTS user_service_request_preference (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category VARCHAR(40) NOT NULL,
    PRIMARY KEY (user_id, category)
);
