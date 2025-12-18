package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.BulkDecisionDTO;
import com.tlu.hrm.dto.BulkDecisionResultDTO;
import com.tlu.hrm.dto.TimekeepingExplanationCreateDTO;
import com.tlu.hrm.dto.TimekeepingExplanationDecisionDTO;
import com.tlu.hrm.dto.TimekeepingExplanationFilterDTO;
import com.tlu.hrm.dto.TimekeepingExplanationResponseDTO;

public interface TimekeepingExplanationService {

	TimekeepingExplanationResponseDTO create(
            TimekeepingExplanationCreateDTO dto
    );

    Page<TimekeepingExplanationResponseDTO> getList(
            TimekeepingExplanationFilterDTO filter,
            int page,
            int size
    );

    TimekeepingExplanationResponseDTO getById(Long id);

    TimekeepingExplanationResponseDTO decide(
            Long id,
            TimekeepingExplanationDecisionDTO dto
    );

    BulkDecisionResultDTO decideMany(BulkDecisionDTO dto);
}
