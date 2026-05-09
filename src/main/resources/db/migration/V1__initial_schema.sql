-- V1: Initial schema for NSS VIIT Platform

CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'ROLE_USER',
    created_at    TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- -------------------------------------------------------

CREATE TABLE IF NOT EXISTS events (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    description TEXT,
    event_date  DATE,
    location    VARCHAR(200),
    category    VARCHAR(50)   NOT NULL,
    created_by  BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_events_category ON events(category);

-- -------------------------------------------------------

CREATE TABLE IF NOT EXISTS polls (
    id          BIGSERIAL PRIMARY KEY,
    event_id    BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    question    VARCHAR(500) NOT NULL,
    expires_at  TIMESTAMP,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_by  BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_polls_event_id   ON polls(event_id);
CREATE INDEX IF NOT EXISTS idx_polls_expires_at ON polls(expires_at);

-- -------------------------------------------------------

CREATE TABLE IF NOT EXISTS poll_options (
    id            BIGSERIAL PRIMARY KEY,
    poll_id       BIGINT       NOT NULL REFERENCES polls(id) ON DELETE CASCADE,
    option_text   VARCHAR(300) NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_poll_options_poll_id ON poll_options(poll_id);

-- -------------------------------------------------------

CREATE TABLE IF NOT EXISTS votes (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT NOT NULL REFERENCES users(id)        ON DELETE CASCADE,
    poll_id        BIGINT NOT NULL REFERENCES polls(id)        ON DELETE CASCADE,
    poll_option_id BIGINT NOT NULL REFERENCES poll_options(id) ON DELETE CASCADE,
    created_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_votes_user_poll UNIQUE(user_id, poll_id)
);

CREATE INDEX IF NOT EXISTS idx_votes_user_id        ON votes(user_id);
CREATE INDEX IF NOT EXISTS idx_votes_poll_id        ON votes(poll_id);
CREATE INDEX IF NOT EXISTS idx_votes_poll_option_id ON votes(poll_option_id);
