-- Xóa cột refresh_token_expiry nếu đang tồn tại
ALTER TABLE users
DROP COLUMN IF EXISTS refresh_token_expiry;

-- Xóa foreign key cũ nếu tồn tại
ALTER TABLE users
DROP CONSTRAINT IF EXISTS fk_user_employee;

-- Tạo lại foreign key mới 
ALTER TABLE users
ADD CONSTRAINT fk_user_employee
    FOREIGN KEY (employee_id)
    REFERENCES employees(id)
    ON DELETE SET NULL;
