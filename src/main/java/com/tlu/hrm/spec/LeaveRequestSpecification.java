package com.tlu.hrm.spec;

import org.springframework.data.jpa.domain.Specification;

import com.tlu.hrm.entities.LeaveRequest;
import com.tlu.hrm.enums.LeaveStatus;
import com.tlu.hrm.enums.LeaveType;

public class LeaveRequestSpecification {

	public static Specification<LeaveRequest> hasEmployeeName(String name) {
        return (root, query, cb) ->
                name == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("employee").get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<LeaveRequest> hasDepartment(String dept) {
        return (root, query, cb) ->
                dept == null ? cb.conjunction() :
                        cb.equal(root.get("employee").get("department"), dept);
    }

    public static Specification<LeaveRequest> hasStatus(LeaveStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() :
                        cb.equal(root.get("status"), status);
    }

    public static Specification<LeaveRequest> hasType(LeaveType type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction() :
                        cb.equal(root.get("type"), type);
    }
}
