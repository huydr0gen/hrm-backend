-- =========================================
-- 1. Tạo bảng departments
-- =========================================
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================
-- 2. Insert dữ liệu phòng ban từ employees cũ
-- =========================================
INSERT INTO departments (name)
SELECT DISTINCT department
FROM employees
WHERE department IS NOT NULL;

-- =========================================
-- 3. Thêm cột department_id vào employees
-- =========================================
ALTER TABLE employees
ADD COLUMN department_id BIGINT;

-- =========================================
-- 4. Map department_id theo dữ liệu cũ
-- =========================================
UPDATE employees e
SET department_id = d.id
FROM departments d
WHERE e.department = d.name;

-- =========================================
-- 5. Set NOT NULL cho department_id
-- =========================================
ALTER TABLE employees
ALTER COLUMN department_id SET NOT NULL;

-- =========================================
-- 6. Thêm foreign key constraint
-- =========================================
ALTER TABLE employees
ADD CONSTRAINT fk_employee_department
FOREIGN KEY (department_id)
REFERENCES departments(id);

-- =========================================
-- 7. Xóa cột department cũ (VARCHAR)
-- =========================================
ALTER TABLE employees
DROP COLUMN department;
