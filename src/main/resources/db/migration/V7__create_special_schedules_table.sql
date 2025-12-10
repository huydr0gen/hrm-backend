CREATE TABLE special_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id BIGINT NOT NULL,
    date DATE NOT NULL,
    shift VARCHAR(100),
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by VARCHAR(100),
    approved_by VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME,
    approved_at DATETIME
);
