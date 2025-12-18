package com.tlu.hrm.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.dto.TimekeepingExplanationFilterDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.TimekeepingExplanation;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class TimekeepingExplanationSpecification {

	public static Specification<TimekeepingExplanation> build(
            TimekeepingExplanationFilterDTO filter,
            String forcedDepartment,
            Long forcedEmployeeId
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // join employee
            Join<TimekeepingExplanation, Employee> employeeJoin =
                    root.join("employee");

            // =====================
            // Filter theo employeeCode
            // =====================
            if (filter.getEmployeeCode() != null && !filter.getEmployeeCode().isBlank()) {
                predicates.add(
                    cb.like(
                        cb.lower(employeeJoin.get("code")),
                        "%" + filter.getEmployeeCode().toLowerCase() + "%"
                    )
                );
            }

            // =====================
            // Filter theo department (HR truyền)
            // =====================
            if (filter.getDepartment() != null && !filter.getDepartment().isBlank()) {
                predicates.add(
                    cb.equal(employeeJoin.get("department"), filter.getDepartment())
                );
            }

            // =====================
            // Ép department cho MANAGER
            // =====================
            if (forcedDepartment != null) {
                predicates.add(
                    cb.equal(employeeJoin.get("department"), forcedDepartment)
                );
            }

            // =====================
            // Ép employee cho EMPLOYEE
            // =====================
            if (forcedEmployeeId != null) {
                predicates.add(
                    cb.equal(employeeJoin.get("id"), forcedEmployeeId)
                );
            }

            // =====================
            // Filter theo ngày công
            // =====================
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

            // =====================
            // Filter theo trạng thái
            // =====================
            if (filter.getStatus() != null) {
                predicates.add(
                    cb.equal(root.get("status"), filter.getStatus())
                );
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
