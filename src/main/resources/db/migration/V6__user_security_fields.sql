ALTER TABLE users
  ADD COLUMN failed_attempts      INT       DEFAULT 0,
  ADD COLUMN lockout_time         TIMESTAMP,
  ADD COLUMN verified             BOOLEAN   DEFAULT FALSE,
  ADD COLUMN verification_token   VARCHAR(64),
  ADD COLUMN reset_token          VARCHAR(64),
  ADD COLUMN reset_token_expiry   TIMESTAMP;
