CREATE TABLE special_schedules (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    shift VARCHAR(100),
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by VARCHAR(100),
    approved_by VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    approved_at TIMESTAMP
);

-- Add FK constraint to employees table
ALTER TABLE special_schedules
ADD CONSTRAINT fk_special_schedule_employee
FOREIGN KEY (employee_id)
REFERENCES employees(id)
ON DELETE CASCADE;
