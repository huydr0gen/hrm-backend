package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.OTRequestCreateDTO;
import com.tlu.hrm.dto.OTRequestResponseDTO;
import com.tlu.hrm.dto.OTResponseDTO;
import com.tlu.hrm.enums.OTRequestStatus;

public interface OTService {

	OTRequestResponseDTO createOT(OTRequestCreateDTO dto);

    void respondOT(Long participantId, OTResponseDTO dto);

    Page<OTRequestResponseDTO> getMyOTs(int page, int size);

    Page<OTRequestResponseDTO> getManagerOTs(OTRequestStatus status, int page,int size);

    void cancelOT(Long otRequestId);
}
