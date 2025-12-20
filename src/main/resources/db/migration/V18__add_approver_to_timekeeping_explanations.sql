-- =====================================================
-- Add approver_id for approval workflow
-- =====================================================

ALTER TABLE timekeeping_explanations
ADD COLUMN IF NOT EXISTS approver_id BIGINT;

-- =====================================================
-- Optional: Foreign key to users table
-- Khi user bị xóa → approver_id = NULL
-- =====================================================

ALTER TABLE timekeeping_explanations
ADD CONSTRAINT fk_tke_approver
FOREIGN KEY (approver_id)
REFERENCES users(id)
ON DELETE SET NULL;
