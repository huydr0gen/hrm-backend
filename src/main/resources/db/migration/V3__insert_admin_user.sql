INSERT INTO employees (code, full_name, date_of_birth, position, department, email, phone_number)
VALUES (
    'EMP001',
    'Admin User',
    '1990-01-01',
    'System Admin',
    'IT',
    'admin@system.com',
    '0123456789'
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (
    username,
    password,
    status,
    employee_id,
    refresh_token,
    refresh_token_expiry,
    created_at,
    updated_at,
    last_login
)
VALUES (
    'admin',
    '$2a$10$PAH7eR1IK.9T6ReSg9Z9v.Hi680OpOejwYfzVJEhNiBAfiTsdogju',
    'ACTIVE',
    (SELECT id FROM employees WHERE code = 'EMP001'),
    NULL,
    NULL,
    NOW(),
    NOW(),
    NULL
)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT 
    (SELECT id FROM users WHERE username = 'admin'),
    (SELECT id FROM roles WHERE name = 'ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM user_roles 
    WHERE user_id = (SELECT id FROM users WHERE username = 'admin')
      AND role_id = (SELECT id FROM roles WHERE name = 'ADMIN')
);