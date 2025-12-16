CREATE TABLE department_approval_requests (
    id BIGSERIAL PRIMARY KEY,

    department VARCHAR(255) NOT NULL,

    created_by BIGINT NOT NULL,

    type VARCHAR(50) NOT NULL,
    content VARCHAR(1000) NOT NULL,

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    decided_by BIGINT,
    decided_at TIMESTAMP,
    decision_note VARCHAR(1000),

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_department_approval_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT fk_department_approval_decided_by
        FOREIGN KEY (decided_by)
        REFERENCES users(id)
);

-- =========================
-- Indexes for performance
-- =========================
CREATE INDEX idx_department_approval_department
    ON department_approval_requests(department);

CREATE INDEX idx_department_approval_status
    ON department_approval_requests(status);

CREATE INDEX idx_department_approval_created_by
    ON department_approval_requests(created_by);
