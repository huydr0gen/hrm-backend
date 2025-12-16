CREATE TABLE personal_approval_requests (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    department VARCHAR(255) NOT NULL,

    type VARCHAR(50) NOT NULL,
    reason VARCHAR(1000) NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    decided_by BIGINT,
    decided_at TIMESTAMP,
    decision_note VARCHAR(1000),

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_personal_approval_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id),

    CONSTRAINT fk_personal_approval_decided_by
        FOREIGN KEY (decided_by)
        REFERENCES users(id)
);

-- =========================
-- Indexes for performance
-- =========================
CREATE INDEX idx_personal_approval_employee
    ON personal_approval_requests(employee_id);

CREATE INDEX idx_personal_approval_department
    ON personal_approval_requests(department);

CREATE INDEX idx_personal_approval_status
    ON personal_approval_requests(status);
