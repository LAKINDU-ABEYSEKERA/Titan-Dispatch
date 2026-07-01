-- Ensure the dev_operator account explicitly possesses the necessary operational authorities
UPDATE system_user
SET role = 'ADMIN',
    status = 'ACTIVE'
WHERE username = 'dev_operator';

-- If your system utilizes a dedicated user_roles mapping table for multiple authorities:
-- INSERT INTO user_roles (user_id, role_name)
-- SELECT id, 'ROLE_ADMIN' FROM system_user WHERE username = 'dev_operator'
-- ON CONFLICT DO NOTHING;