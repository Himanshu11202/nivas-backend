-- Ensure SOCIETY_ADMIN is allowed (idempotent for Flyway if enabled later)
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role IN ('RESIDENT', 'ADMIN', 'GUARD', 'SUPER_ADMIN', 'SOCIETY_ADMIN', 'WORKER'));
