package com.tlu.hrm.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tlu.hrm.entities.TimekeepingExplanation;

public interface TimekeepingExplanationRepository extends JpaRepository<TimekeepingExplanation, Long>, 
JpaSpecificationExecutor<TimekeepingExplanation> {

	@Query("""
	        SELECT te
	        FROM TimekeepingExplanation te
	        WHERE te.employee.id = :employeeId
	          AND te.workDate = :date
	          AND te.status = 'APPROVED'
	    """)
	    Optional<TimekeepingExplanation> findApprovedByEmployeeAndDate(
	            @Param("employeeId") Long employeeId,
	            @Param("date") LocalDate date
	    );
}
