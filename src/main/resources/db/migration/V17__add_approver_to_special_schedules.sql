-- =====================================================
-- Add approver_id for approval workflow
-- =====================================================

ALTER TABLE special_schedules
ADD COLUMN IF NOT EXISTS approver_id BIGINT;

-- =====================================================
-- Optional: FK tới users (khuyến nghị)
-- Nếu bạn muốn rollback approver khi user bị xóa
-- =====================================================

ALTER TABLE special_schedules
ADD CONSTRAINT fk_special_schedule_approver
FOREIGN KEY (approver_id)
REFERENCES users(id)
ON DELETE SET NULL;
