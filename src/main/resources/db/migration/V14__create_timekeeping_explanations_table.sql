CREATE TABLE timekeeping_explanations (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,

    work_date DATE NOT NULL,

    -- dữ liệu công gốc (có thể null nếu chưa có)
    original_check_in TIME,
    original_check_out TIME,

    -- dữ liệu đề xuất chỉnh sửa
    proposed_check_in TIME,
    proposed_check_out TIME,

    reason TEXT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    decided_by BIGINT,
    decided_at TIMESTAMP,
    manager_note TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- FK
ALTER TABLE timekeeping_explanations
ADD CONSTRAINT fk_timekeeping_explanations_employee
FOREIGN KEY (employee_id)
REFERENCES employees(id);

-- Index cho filter
CREATE INDEX idx_tke_employee_id
ON timekeeping_explanations(employee_id);

CREATE INDEX idx_tke_work_date
ON timekeeping_explanations(work_date);

CREATE INDEX idx_tke_status
ON timekeeping_explanations(status);
