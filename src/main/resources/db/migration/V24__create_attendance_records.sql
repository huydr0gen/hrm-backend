CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    work_date DATE NOT NULL,

    check_in TIME,
    check_out TIME,

    worked_minutes INT,
    paid_minutes INT,

    work_type VARCHAR(50),
    note VARCHAR(255),

    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,

    CONSTRAINT uq_attendance_employee_date
        UNIQUE (employee_id, work_date),

    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
);
