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
            // 1) Filter theo employeeId (dùng cho EMPLOYEE hoặc filter riêng)
            // ======================================================
            if (f.getEmployeeId() != null) {
                predicates.add(cb.equal(root.get("employeeId"), f.getEmployeeId()));
            }

            // ======================================================
            // 2) Filter theo danh sách employeeIds (dùng cho MANAGER)
            // ======================================================
            if (f.getEmployeeIds() != null && !f.getEmployeeIds().isEmpty()) {
                predicates.add(root.get("employeeId").in(f.getEmployeeIds()));
            }

            // ======================================================
            // 3) Filter theo khoảng ngày
            // ======================================================
            LocalDate dateFrom = f.getDateFrom();
            LocalDate dateTo = f.getDateTo();

            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
            }

            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), dateTo));
            }

            // ======================================================
            // 4) Filter theo trạng thái
            // ======================================================
            SpecialScheduleStatus status = f.getStatus();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
