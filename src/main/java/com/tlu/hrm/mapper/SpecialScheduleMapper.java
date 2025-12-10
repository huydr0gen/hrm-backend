package com.tlu.hrm.mapper;

import java.time.LocalDateTime;

import com.tlu.hrm.dto.SpecialScheduleCreateDTO;
import com.tlu.hrm.dto.SpecialScheduleResponseDTO;
import com.tlu.hrm.dto.SpecialScheduleUpdateDTO;
import com.tlu.hrm.entities.SpecialSchedule;
import com.tlu.hrm.enums.SpecialScheduleStatus;

public class SpecialScheduleMapper {

	public SpecialSchedule toEntity(SpecialScheduleCreateDTO dto, String createdBy) {
        SpecialSchedule e = new SpecialSchedule();
        e.setEmployeeId(dto.getEmployeeId());
        e.setDate(dto.getDate());
        e.setShift(dto.getShift());
        e.setReason(dto.getReason());
        e.setStatus(SpecialScheduleStatus.PENDING);
        e.setCreatedBy(createdBy);
        e.setCreatedAt(LocalDateTime.now());
        return e;
    }

    public void updateEntity(SpecialSchedule e, SpecialScheduleUpdateDTO dto) {
        e.setDate(dto.getDate());
        e.setShift(dto.getShift());
        e.setReason(dto.getReason());
        e.setUpdatedAt(LocalDateTime.now());
    }

    public SpecialScheduleResponseDTO toResponse(SpecialSchedule e) {
        SpecialScheduleResponseDTO dto = new SpecialScheduleResponseDTO();

        dto.setId(e.getId());
        dto.setEmployeeId(e.getEmployeeId());
        dto.setDate(e.getDate());
        dto.setShift(e.getShift());
        dto.setReason(e.getReason());
        dto.setStatus(e.getStatus());

        dto.setCreatedBy(e.getCreatedBy());
        dto.setApprovedBy(e.getApprovedBy());

        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        dto.setApprovedAt(e.getApprovedAt());

        return dto;
    }
}
