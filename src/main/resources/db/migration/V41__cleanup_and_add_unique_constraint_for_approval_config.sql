-- =====================================================
-- Step 1: Deactivate duplicate approval configs
-- Keep the latest record (by created_at)
-- =====================================================

WITH ranked AS (
    SELECT
        id,
        target_type,
        target_id,
        created_at,
        ROW_NUMBER() OVER (
            PARTITION BY target_type, target_id
            ORDER BY created_at DESC
        ) AS rn
    FROM approval_config
    WHERE active = true
)
UPDATE approval_config
SET active = false
WHERE id IN (
    SELECT id
    FROM ranked
    WHERE rn > 1
);

-- =====================================================
-- Step 2: Add unique constraint
-- =====================================================

ALTER TABLE approval_config
ADD CONSTRAINT uq_approval_config_target
UNIQUE (target_type, target_id, active);

-- =====================================================
-- Step 3: Add index for faster lookup (optional but recommended)
-- =====================================================

CREATE INDEX IF NOT EXISTS idx_approval_config_target_lookup
ON approval_config (target_type, target_id, active);
