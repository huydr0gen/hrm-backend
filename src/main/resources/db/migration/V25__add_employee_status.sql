ALTER TABLE employees
ADD COLUMN status VARCHAR(20);

UPDATE employees
SET status = 'ACTIVE'
WHERE status IS NULL;

ALTER TABLE employees
ALTER COLUMN status SET NOT NULL;