package com.tlu.hrm.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.SpecialScheduleStatus;

import jakarta.persistence.criteria.Predicate;

public class SpecialScheduleSpecification {

	public static Specification<SpecialSchedule> build(SpecialScheduleFilterDTO f) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (f.getEmployeeId() != null) {
                predicates.add(
                        cb.equal(root.get("employee").get("id"), f.getEmployeeId())
                );
            }

            if (f.getEmployeeIds() != null && !f.getEmployeeIds().isEmpty()) {
                predicates.add(
                        root.get("employee").get("id").in(f.getEmployeeIds())
                );
            }

            if (f.getDepartmentId() != null) {
                predicates.add(
                        cb.equal(
                                root.get("employee").get("department").get("id"),
                                f.getDepartmentId()
                        )
                );
            }

            // =========================
            // Date overlap filter
            // startDate <= dateTo AND (endDate IS NULL OR endDate >= dateFrom)
            // =========================
            if (f.getDateFrom() != null && f.getDateTo() != null) {
                predicates.add(
                        cb.and(
                                cb.lessThanOrEqualTo(
                                        root.get("startDate"),
                                        f.getDateTo()
                                ),
                                cb.or(
                                        cb.isNull(root.get("endDate")),
                                        cb.greaterThanOrEqualTo(
                                                root.get("endDate"),
                                                f.getDateFrom()
                                        )
                                )
                        )
                );
            }

            if (f.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), f.getStatus())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
	
	public static Specification<SpecialSchedule> buildForApprover(
	        Set<Long> approvedEmployeeIds,
	        Set<Long> approvedDepartmentIds
	) {
	    return (root, query, cb) -> {

	        List<Predicate> predicates = new ArrayList<>();

	        // ===== CHỈ LẤY PENDING =====
	        predicates.add(
	                cb.equal(
	                        root.get("status"),
	                        SpecialScheduleStatus.PENDING
	                )
	        );

	        // ===== QUYỀN DUYỆT (OR – CỘNG DỒN) =====
	        List<Predicate> approvalPredicates = new ArrayList<>();

	        if (approvedEmployeeIds != null && !approvedEmployeeIds.isEmpty()) {
	            approvalPredicates.add(
	                    root.get("employee")
	                        .get("id")
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
	            // Không có quyền duyệt → không trả dữ liệu
	            predicates.add(cb.disjunction());
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}
}
