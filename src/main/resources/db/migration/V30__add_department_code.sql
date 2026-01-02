-- =====================================================
-- Add department code (DEPxxx)
-- =====================================================

-- 1. Add column (nullable first)
ALTER TABLE departments
ADD COLUMN code VARCHAR(6);

-- 2. Update existing departments (SAFE WAY: by id)
-- ⚠️ Adjust ID values according to your actual data

UPDATE departments SET code = 'DEP001' WHERE id = 1;
UPDATE departments SET code = 'DEP002' WHERE id = 2;
UPDATE departments SET code = 'DEP003' WHERE id = 3;
UPDATE departments SET code = 'DEP004' WHERE id = 4;
UPDATE departments SET code = 'DEP005' WHERE id = 5;
UPDATE departments SET code = 'DEP006' WHERE id = 6;
UPDATE departments SET code = 'DEP007' WHERE id = 7;
UPDATE departments SET code = 'DEP008' WHERE id = 8;

-- 3. Ensure no null code remains
UPDATE departments
SET code = 'DEP999'
WHERE code IS NULL;

-- 4. Add constraints
ALTER TABLE departments
ALTER COLUMN code SET NOT NULL;

ALTER TABLE departments
ADD CONSTRAINT uk_departments_code UNIQUE (code);
