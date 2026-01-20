ALTER TABLE employee_certificates
ADD COLUMN status VARCHAR(20);

UPDATE employee_certificates
SET status = 'ACTIVE'
WHERE status IS NULL;

ALTER TABLE employee_certificates
ALTER COLUMN status SET NOT NULL;

ALTER TABLE employee_certificates
ADD CONSTRAINT chk_employee_certificates_status
CHECK (status IN ('ACTIVE', 'EXPIRED'));
