-- =====================================================
-- Add working_hours for special schedule rules
-- =====================================================
ALTER TABLE special_schedules
ADD COLUMN working_hours INTEGER;