-- 1. Thêm cột mới
ALTER TABLE leave_requests ADD COLUMN leave_date DATE;

-- 2. Copy dữ liệu từ start_date sang leave_date
UPDATE leave_requests SET leave_date = start_date;

-- 3. Set NOT NULL (sau khi đã có data)
ALTER TABLE leave_requests ALTER COLUMN leave_date SET NOT NULL;

-- 4. Xóa cột cũ
ALTER TABLE leave_requests DROP COLUMN start_date;
ALTER TABLE leave_requests DROP COLUMN end_date;
