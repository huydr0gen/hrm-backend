package com.tlu.hrm.spec;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

public class LeaveRequestSpecification {

	// =================================
    // Filter theo tên nhân viên
    // =================================
    public static Specification<LeaveRequest> hasEmployeeName(String name) {
        return (root, query, cb) ->
                (name == null || name.isBlank())
                        ? cb.conjunction()
                        : cb.like(
                                cb.lower(root.get("employee").get("fullName")),
                                "%" + name.toLowerCase() + "%"
                        );
    }

    // =================================
    // ✅ Filter theo phòng ban (Department entity)
    // =================================
    public static Specification<LeaveRequest> hasDepartment(Long departmentId) {
        return (root, query, cb) ->
            departmentId == null
                ? cb.conjunction()
                : cb.equal(
                    root.get("employee")
                        .get("department")
                        .get("id"),
                    departmentId
                );
    }

    // =================================
    // Filter theo trạng thái
    // =================================
    public static Specification<LeaveRequest> hasStatus(LeaveStatus status) {
        return (root, query, cb) ->
                status == null
                        ? cb.conjunction()
                        : cb.equal(root.get("status"), status);
    }

    // =================================
    // Filter theo loại nghỉ
    // =================================
    public static Specification<LeaveRequest> hasType(LeaveType type) {
        return (root, query, cb) ->
                type == null
                        ? cb.conjunction()
                        : cb.equal(root.get("type"), type);
    }
}
