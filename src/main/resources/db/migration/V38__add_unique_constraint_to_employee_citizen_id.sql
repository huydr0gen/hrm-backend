-- Thêm unique constraint
ALTER TABLE employees
ADD CONSTRAINT uk_employees_citizen_id UNIQUE (citizen_id);