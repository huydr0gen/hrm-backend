package com.tlu.hrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tlu.hrm.entities.LeaveRequest;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long>,
JpaSpecificationExecutor<LeaveRequest> {

}
