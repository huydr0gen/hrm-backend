CREATE TABLE employee_certificates (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,

    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255),

    issued_date DATE,
    expired_date DATE,

    note VARCHAR(500),

    created_at TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_employee_certificate_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
);
