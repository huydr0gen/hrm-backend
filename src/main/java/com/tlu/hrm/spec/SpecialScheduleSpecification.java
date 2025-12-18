package com.tlu.hrm.spec;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.SpecialScheduleStatus;

import jakarta.persistence.criteria.Predicate;

public class SpecialScheduleSpecification {

	public static Specification<SpecialSchedule> build(SpecialScheduleFilterDTO f) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ======================================================
            // 1) Filter theo employeeId
            // ======================================================
            if (f.getEmployeeId() != null) {
                predicates.add(
                        cb.equal(root.get("employee").get("id"), f.getEmployeeId())
                );
            }

            // ======================================================
            // 2) Filter theo danh sách employeeIds (MANAGER)
            // ======================================================
            if (f.getEmployeeIds() != null && !f.getEmployeeIds().isEmpty()) {
                predicates.add(
                        root.get("employee").get("id").in(f.getEmployeeIds())
                );
            }

            // ======================================================
            // 3) Filter theo khoảng ngày (OVERLAP LOGIC)
            // ======================================================
            if (f.getDateFrom() != null && f.getDateTo() != null) {
                predicates.add(
                        cb.and(
                                cb.lessThanOrEqualTo(
                                        root.get("startDate"),
                                        f.getDateTo()
                                ),
                                cb.greaterThanOrEqualTo(
                                        root.get("endDate"),
                                        f.getDateFrom()
                                )
                        )
                );
            } else if (f.getDateFrom() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("endDate"),
                                f.getDateFrom()
                        )
                );
            } else if (f.getDateTo() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("startDate"),
                                f.getDateTo()
                        )
                );
            }

            // ======================================================
            // 4) Filter theo trạng thái
            // ======================================================
            if (f.getStatus() != null) {
                predicates.add(
                        cb.equal(root.get("status"), f.getStatus())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
