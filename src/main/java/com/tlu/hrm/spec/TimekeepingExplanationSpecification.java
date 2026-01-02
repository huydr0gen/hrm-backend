package com.tlu.hrm.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.dto.TimekeepingExplanationFilterDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.TimekeepingExplanation;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class TimekeepingExplanationSpecification {

	public static Specification<TimekeepingExplanation> buildForApprover(
            TimekeepingExplanationFilterDTO filter,
            Set<Long> approvedEmployeeIds,
            Set<Long> approvedDepartmentIds
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<TimekeepingExplanation, Employee> employeeJoin =
                    root.join("employee");

            // =====================
            // QUYỀN DUYỆT (CỘNG DỒN)
            // =====================
            List<Predicate> approvalPredicates = new ArrayList<>();

            if (approvedEmployeeIds != null && !approvedEmployeeIds.isEmpty()) {
                approvalPredicates.add(
                        employeeJoin.get("id").in(approvedEmployeeIds)
                );
            }

            if (approvedDepartmentIds != null && !approvedDepartmentIds.isEmpty()) {
                approvalPredicates.add(
                        employeeJoin.get("department").get("id")
                                .in(approvedDepartmentIds)
                );
            }

            if (!approvalPredicates.isEmpty()) {
                predicates.add(
                        cb.or(approvalPredicates.toArray(new Predicate[0]))
                );
            }

            // =====================
            // FILTER NGHIỆP VỤ
            // =====================
            if (filter.getEmployeeCode() != null && !filter.getEmployeeCode().isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(employeeJoin.get("code")),
                                "%" + filter.getEmployeeCode().toLowerCase() + "%"
                        )
                );
            }

            if (filter.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("workDate"),
                                filter.getFromDate()
                        )
                );
            }

            if (filter.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("workDate"),
                                filter.getToDate()
                        )
                );
            }

            if (filter.getStatus() != null) {
                predicates.add(
                        cb.equal(
                                root.get("status"),
                                filter.getStatus()
                        )
                );
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
