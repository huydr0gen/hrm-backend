-- 1. Thêm cột (cho phép null tạm thời)
ALTER TABLE leave_requests
ADD COLUMN duration VARCHAR(20);

-- 2. Gán giá trị mặc định cho data cũ
UPDATE leave_requests
SET duration = 'FULL_DAY'
WHERE duration IS NULL;

-- 3. Set NOT NULL
ALTER TABLE leave_requests
ALTER COLUMN duration SET NOT NULL;
