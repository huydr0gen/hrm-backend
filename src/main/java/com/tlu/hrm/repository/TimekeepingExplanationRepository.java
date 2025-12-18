package com.tlu.hrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tlu.hrm.entities.TimekeepingExplanation;

public interface TimekeepingExplanationRepository extends JpaRepository<TimekeepingExplanation, Long>, 
JpaSpecificationExecutor<TimekeepingExplanation> {

}
