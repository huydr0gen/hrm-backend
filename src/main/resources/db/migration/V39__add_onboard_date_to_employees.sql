-- V20260111_01__add_onboard_date_to_employees.sql

ALTER TABLE employees
ADD COLUMN onboard_date DATE;

UPDATE employees
SET onboard_date = created_at::date
WHERE onboard_date IS NULL;

ALTER TABLE employees
ALTER COLUMN onboard_date SET NOT NULL;
