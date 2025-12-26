package com.tlu.hrm.service;

import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.ApprovalConfigCreateDTO;
import com.tlu.hrm.dto.ApprovalConfigDTO;
import com.tlu.hrm.entities.ApprovalConfig;
import com.tlu.hrm.repository.ApprovalConfigRepository;

@Service
public class ApprovalConfigServiceImpl implements ApprovalConfigService {

	private final ApprovalConfigRepository repository;

	public ApprovalConfigServiceImpl(ApprovalConfigRepository repository) {
		super();
		this.repository = repository;
	}
	
	@Override
    public ApprovalConfigDTO createOrUpdate(ApprovalConfigCreateDTO dto) {

		if (dto.getTargetType() == null
				|| dto.getTargetId() == null
			    || dto.getApproverId() == null) {
		    throw new RuntimeException("Thiếu dữ liệu thiết lập người duyệt");
		}
		
        ApprovalConfig config = repository
            .findByTargetTypeAndTargetIdAndActiveTrue(
                dto.getTargetType(),
                dto.getTargetId()
            )
            .orElseGet(() ->
                new ApprovalConfig(
                    dto.getTargetType(),
                    dto.getTargetId(),
                    dto.getApproverId()
                )
            );

        config.setApproverId(dto.getApproverId());
        config.setActive(true);

        ApprovalConfig saved = repository.save(config);
        return mapToDTO(saved);
    }

    private ApprovalConfigDTO mapToDTO(ApprovalConfig config) {

        ApprovalConfigDTO dto = new ApprovalConfigDTO();
        dto.setId(config.getId());
        dto.setTargetType(config.getTargetType());
        dto.setTargetId(config.getTargetId());
        dto.setApproverId(config.getApproverId());
        dto.setActive(config.isActive());

        return dto;
    }
}
