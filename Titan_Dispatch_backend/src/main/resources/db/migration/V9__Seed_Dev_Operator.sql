-- Enable PostgreSQL native encryption functions
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Insert the development user (adjust column names if your entity differs)
INSERT INTO system_user (id, username, password, role, status, created_at)
VALUES (
    gen_random_uuid(),
    'dev_operator',
    crypt('titan123', gen_salt('bf', 10)), -- Generates Spring-compatible BCrypt hash
    'ADMIN',
    'ACTIVE',
    CURRENT_TIMESTAMP
)
-- If the user already exists, ensure the password is set correctly to 'titan123'
ON CONFLICT (username) DO UPDATE
SET password = crypt('titan123', gen_salt('bf', 10)),
    status = 'ACTIVE';