package com.tlu.hrm.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tlu.hrm.dto.SpecialScheduleApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleBulkApproveDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;

public interface SpecialScheduleService {

	Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter, Pageable pageable);

    SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto);

    SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto);

    SpecialScheduleResponseDTO approve(Long id, SpecialScheduleApproveDTO dto);

    int approveMany(SpecialScheduleBulkApproveDTO dto);

    SpecialScheduleResponseDTO detail(Long id);
}
