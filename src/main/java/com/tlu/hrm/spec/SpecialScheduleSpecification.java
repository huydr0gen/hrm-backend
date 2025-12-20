package com.tlu.hrm.spec;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.entities.SpecialSchedule;

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

            if (f.getDateFrom() != null && f.getDateTo() != null) {
                predicates.add(
                        cb.and(
                                cb.lessThanOrEqualTo(root.get("startDate"), f.getDateTo()),
                                cb.greaterThanOrEqualTo(root.get("endDate"), f.getDateFrom())
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
}
