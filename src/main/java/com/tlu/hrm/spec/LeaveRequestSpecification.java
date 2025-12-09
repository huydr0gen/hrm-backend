package com.tlu.hrm.spec;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

public class LeaveRequestSpecification {

	public static Specification<LeaveRequest> hasEmployeeName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                cb.like(cb.lower(root.join("employee").get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<LeaveRequest> hasDepartment(String department) {
        return (root, query, cb) ->
                department == null ? null :
                cb.equal(root.join("employee").get("department"), department);
    }

    public static Specification<LeaveRequest> hasStatus(LeaveStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<LeaveRequest> hasType(LeaveType type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<LeaveRequest> createdAfter(LocalDate date) {
        return (root, query, cb) ->
                date == null ? null : cb.greaterThanOrEqualTo(root.get("startDate"), date);
    }

    public static Specification<LeaveRequest> createdBefore(LocalDate date) {
        return (root, query, cb) ->
                date == null ? null : cb.lessThanOrEqualTo(root.get("endDate"), date);
    }
}
