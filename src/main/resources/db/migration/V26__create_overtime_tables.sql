-- =====================================================
-- TABLE: ot_requests
-- =====================================================
CREATE TABLE ot_requests (
    id BIGSERIAL PRIMARY KEY,

    manager_id BIGINT NOT NULL,
    ot_date DATE NOT NULL,

    start_time TIME NOT NULL,
    end_time TIME NOT NULL,

    reason TEXT,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ot_requests_manager
        FOREIGN KEY (manager_id)
        REFERENCES employees(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_ot_requests_manager_id
    ON ot_requests(manager_id);

CREATE INDEX idx_ot_requests_status
    ON ot_requests(status);

CREATE INDEX idx_ot_requests_created_at
    ON ot_requests(created_at);

-- =====================================================
-- TABLE: ot_participants
-- =====================================================
CREATE TABLE ot_participants (
    id BIGSERIAL PRIMARY KEY,

    ot_request_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,

    status VARCHAR(30) NOT NULL,

    reject_reason TEXT,
    responded_at TIMESTAMP,

    CONSTRAINT fk_ot_participants_request
        FOREIGN KEY (ot_request_id)
        REFERENCES ot_requests(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_ot_participants_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE RESTRICT,

    CONSTRAINT uk_ot_request_employee
        UNIQUE (ot_request_id, employee_id)
);

CREATE INDEX idx_ot_participants_employee_id
    ON ot_participants(employee_id);

CREATE INDEX idx_ot_participants_status
    ON ot_participants(status);
