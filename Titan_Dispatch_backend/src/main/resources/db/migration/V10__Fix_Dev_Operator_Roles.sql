-- Ensure the dev_operator account explicitly possesses the necessary operational authorities
UPDATE system_user
SET role = 'ADMIN',
    is_active = TRUE
WHERE username = 'dev_operator';