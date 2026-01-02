package com.tlu.hrm.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.entities.Department;
import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

import jakarta.persistence.criteria.Predicate;

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
    
    public static Specification<LeaveRequest> buildForApprover(
            Set<Long> approvedEmployeeIds,
            Set<Long> approvedDepartmentIds
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ===== CHỈ LẤY ĐƠN PENDING =====
            predicates.add(
                    cb.equal(root.get("status"), LeaveStatus.PENDING)
            );

            // ===== QUYỀN DUYỆT (OR – CỘNG DỒN) =====
            List<Predicate> approvalPredicates = new ArrayList<>();

            if (approvedEmployeeIds != null && !approvedEmployeeIds.isEmpty()) {
                approvalPredicates.add(
                        root.get("employee").get("id")
                                .in(approvedEmployeeIds)
                );
            }

            if (approvedDepartmentIds != null && !approvedDepartmentIds.isEmpty()) {
                approvalPredicates.add(
                        root.get("employee")
                            .get("department")
                            .get("id")
                            .in(approvedDepartmentIds)
                );
            }

            if (!approvalPredicates.isEmpty()) {
                predicates.add(
                        cb.or(approvalPredicates.toArray(new Predicate[0]))
                );
            } else {
                // không có quyền duyệt → không trả gì
                predicates.add(cb.disjunction());
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
