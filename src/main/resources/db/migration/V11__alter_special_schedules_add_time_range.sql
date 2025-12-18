-- =========================
-- Date range
-- =========================
ALTER TABLE special_schedules
ADD COLUMN start_date DATE,
ADD COLUMN end_date DATE;

-- =========================
-- On-site time range
-- =========================
ALTER TABLE special_schedules
ADD COLUMN morning_start TIME,
ADD COLUMN morning_end TIME,
ADD COLUMN afternoon_start TIME,
ADD COLUMN afternoon_end TIME;

-- =========================
-- Type
-- =========================
ALTER TABLE special_schedules
ADD COLUMN type VARCHAR(30);

-- =========================
-- Migrate old data (if any)
-- =========================
UPDATE special_schedules
SET start_date = date
WHERE start_date IS NULL;

-- =========================
-- Constraints
-- =========================
ALTER TABLE special_schedules
ALTER COLUMN start_date SET NOT NULL;

ALTER TABLE special_schedules
ALTER COLUMN type SET NOT NULL;

-- (Optional) drop legacy column nếu không dùng nữa
-- ALTER TABLE special_schedules DROP COLUMN date;
