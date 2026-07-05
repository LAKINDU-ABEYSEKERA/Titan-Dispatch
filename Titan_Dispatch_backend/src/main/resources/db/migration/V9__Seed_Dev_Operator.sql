-- Enable PostgreSQL native encryption functions
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Insert the development user using the correct password_hash and is_active columns
INSERT INTO system_user (id, username, password_hash, role, is_active, created_at)
VALUES (
    gen_random_uuid(),
    'dev_operator',
    crypt('titan123', gen_salt('bf', 10)), -- Generates Spring-compatible BCrypt hash
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP
)
-- If the user already exists, ensure the password hash and active state are set correctly
ON CONFLICT (username) DO UPDATE
SET password_hash = crypt('titan123', gen_salt('bf', 10)),
    is_active = TRUE;