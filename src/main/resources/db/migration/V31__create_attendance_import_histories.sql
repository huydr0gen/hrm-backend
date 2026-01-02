CREATE TABLE attendance_import_histories (
    id BIGSERIAL PRIMARY KEY,

    -- Tháng import, ví dụ: 2026-01
    month VARCHAR(7) NOT NULL,

    -- Tên file import
    file_name VARCHAR(255) NOT NULL,

    -- Đường dẫn file (nếu có lưu)
    file_path VARCHAR(500),

    -- Người thực hiện import (employee.id)
    created_by BIGINT NOT NULL,

    -- Thời điểm import
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendance_import_history_employee
        FOREIGN KEY (created_by)
        REFERENCES employees(id)
);
