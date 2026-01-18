-- 1. Thêm cột status
ALTER TABLE employee_certificates
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- 2. Cập nhật dữ liệu cũ: nếu đã hết hạn thì chuyển sang EXPIRED
UPDATE employee_certificates
SET status = 'EXPIRED'
WHERE expired_date IS NOT NULL
  AND expired_date < CURRENT_DATE;

-- 3. (Optional nhưng khuyên dùng) Thêm check constraint để tránh giá trị sai
ALTER TABLE employee_certificates
ADD CONSTRAINT chk_employee_certificates_status
CHECK (status IN ('ACTIVE', 'EXPIRED'));
