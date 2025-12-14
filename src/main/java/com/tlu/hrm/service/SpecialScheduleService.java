package com.tlu.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleFilterDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.enums.DecisionAction;

public interface SpecialScheduleService {

	Page<SpecialScheduleResponseDTO> list(SpecialScheduleFilterDTO filter);

    SpecialScheduleResponseDTO create(SpecialScheduleCreateDTO dto);

    SpecialScheduleResponseDTO update(Long id, SpecialScheduleUpdateDTO dto);

    SpecialScheduleResponseDTO decide(Long id, DecisionAction action);

    BulkDecisionResultDTO decideMany(List<Long> ids, DecisionAction action);

    SpecialScheduleResponseDTO detail(Long id);
}
