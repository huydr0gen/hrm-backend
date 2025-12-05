INSERT INTO roles (name, description) VALUES
('ADMIN', 'Quản trị hệ thống')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('MANAGER', 'Trưởng phòng / quản lý')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('HR', 'Nhân viên phòng nhân sự')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('EMPLOYEE', 'Nhân viên thường')
ON CONFLICT (name) DO NOTHING;
