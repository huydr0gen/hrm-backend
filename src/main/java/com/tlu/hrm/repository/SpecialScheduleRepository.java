package com.tlu.hrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tlu.hrm.entities.SpecialSchedule;

public interface SpecialScheduleRepository 
extends JpaRepository<SpecialSchedule, Long>, JpaSpecificationExecutor<SpecialSchedule> {

}
