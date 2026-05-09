-- V3: Blood requests and donor interest tables

CREATE TABLE IF NOT EXISTS blood_requests (
    id             BIGSERIAL PRIMARY KEY,
    patient_name   VARCHAR(100)  NOT NULL,
    blood_group    VARCHAR(10)   NOT NULL,
    units_needed   INTEGER       NOT NULL DEFAULT 1,
    hospital       VARCHAR(200)  NOT NULL,
    city           VARCHAR(100)  NOT NULL,
    district       VARCHAR(100)  NOT NULL,
    state          VARCHAR(100)  NOT NULL DEFAULT 'Maharashtra',
    latitude       DECIMAL(10, 7),
    longitude      DECIMAL(10, 7),
    contact_name   VARCHAR(100)  NOT NULL,
    contact_phone  VARCHAR(15)   NOT NULL,
    contact_email  VARCHAR(255),
    description    TEXT,
    urgency        VARCHAR(20)   NOT NULL DEFAULT 'STANDARD',
    deadline       DATE          NOT NULL,
    status         VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
    created_by     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_br_status      ON blood_requests(status);
CREATE INDEX IF NOT EXISTS idx_br_deadline    ON blood_requests(deadline);
CREATE INDEX IF NOT EXISTS idx_br_blood_group ON blood_requests(blood_group);
CREATE INDEX IF NOT EXISTS idx_br_district    ON blood_requests(district);

CREATE TABLE IF NOT EXISTS donor_interests (
    id         BIGSERIAL PRIMARY KEY,
    request_id BIGINT       NOT NULL REFERENCES blood_requests(id) ON DELETE CASCADE,
    name       VARCHAR(100) NOT NULL,
    phone      VARCHAR(15)  NOT NULL,
    email      VARCHAR(255),
    message    VARCHAR(500),
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_di_request_id ON donor_interests(request_id);
