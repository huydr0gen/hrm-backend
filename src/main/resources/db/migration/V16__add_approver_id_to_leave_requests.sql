-- =========================================
-- 1. Thêm cột approver_id (cho phép NULL tạm thời)
-- =========================================
ALTER TABLE leave_requests
ADD COLUMN IF NOT EXISTS approver_id BIGINT;

-- =========================================
-- 2. Tạm thời set approver_id cho dữ liệu cũ
-- (dùng employee_id làm placeholder an toàn)
-- =========================================
UPDATE leave_requests
SET approver_id = employee_id
WHERE approver_id IS NULL;

-- =========================================
-- 3. Set NOT NULL cho approver_id
-- =========================================
ALTER TABLE leave_requests
ALTER COLUMN approver_id SET NOT NULL;

-- =========================================
-- 4. (OPTIONAL) Thêm index để query nhanh
-- =========================================
CREATE INDEX IF NOT EXISTS idx_leave_requests_approver_id
ON leave_requests(approver_id);
