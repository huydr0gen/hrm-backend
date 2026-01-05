-- =====================================================
-- 1. ADD COLUMNS (SAFE)
-- =====================================================
ALTER TABLE approval_configs
ADD COLUMN IF NOT EXISTS target_code VARCHAR(50);

ALTER TABLE approval_configs
ADD COLUMN IF NOT EXISTS approver_code VARCHAR(50);

-- =====================================================
-- 2. MIGRATE DATA: TARGET_CODE
-- =====================================================

-- EMPLOYEE target
UPDATE approval_configs ac
SET target_code = e.code
FROM employees e
WHERE ac.target_type = 'EMPLOYEE'
  AND ac.target_id = e.id
  AND ac.target_code IS NULL;

-- DEPARTMENT target
UPDATE approval_configs ac
SET target_code = d.code
FROM departments d
WHERE ac.target_type = 'DEPARTMENT'
  AND ac.target_id = d.id
  AND ac.target_code IS NULL;

-- =====================================================
-- 3. MIGRATE DATA: APPROVER_CODE
-- =====================================================
UPDATE approval_configs ac
SET approver_code = e.code
FROM employees e
WHERE ac.approver_id = e.id
  AND ac.approver_code IS NULL;

