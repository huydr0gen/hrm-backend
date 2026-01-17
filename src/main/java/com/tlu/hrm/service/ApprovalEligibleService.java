package com.tlu.hrm.service;

import java.util.List;

import com.tlu.hrm.dto.EligibleApproverDTO;
import com.tlu.hrm.enums.ApprovalTargetType;

public interface ApprovalEligibleService {

	List<EligibleApproverDTO> getEligibleApprovers(
	        ApprovalTargetType targetType,
	        String targetCode
	    );
}
