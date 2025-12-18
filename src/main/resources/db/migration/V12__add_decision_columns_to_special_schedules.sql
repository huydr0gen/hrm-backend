ALTER TABLE special_schedules
ADD COLUMN decided_by BIGINT,
ADD COLUMN decided_at TIMESTAMP;
