-- =====================================================
-- Add ON_SITE project information fields
-- =====================================================

ALTER TABLE special_schedules
ADD COLUMN project_code VARCHAR(100),
ADD COLUMN project_name VARCHAR(255),
ADD COLUMN onsite_manager_code VARCHAR(50),
ADD COLUMN onsite_manager_name VARCHAR(255);

-- =====================================================
-- Notes:
-- - Các cột này chỉ áp dụng cho type = 'ON_SITE'
-- - Các loại lịch khác (MATERNITY, CHILD_CARE, OTHER) để NULL
-- - Không thêm NOT NULL để tránh lỗi dữ liệu cũ
-- =====================================================
