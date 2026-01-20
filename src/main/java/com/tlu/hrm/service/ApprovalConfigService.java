package com.tlu.hrm.service;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;

public interface ApprovalConfigService {

	ApprovalConfigDTO createOrUpdate(ApprovalConfigCreateDTO dto);
	
	void deleteById(Long id);
}
