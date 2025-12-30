CREATE TABLE salary_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,

    basic_salary BIGINT,
    allowance BIGINT,
    ot_pay BIGINT,
    bonus BIGINT,
    deduction BIGINT,
    total_salary BIGINT NOT NULL,

    imported_at DATE NOT NULL,

    CONSTRAINT uk_salary_emp_month UNIQUE (employee_id, month, year),
    CONSTRAINT fk_salary_emp FOREIGN KEY (employee_id)
        REFERENCES employees(id)
);
