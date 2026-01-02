package com.tlu.hrm.service;

import org.springframework.data.domain.Page;

import com.tlu.hrm.dto.AttendanceImportHistoryResponseDTO;

public interface AttendanceImportHistoryService {

	Page<AttendanceImportHistoryResponseDTO> getByMonth(
            String month,
            int page,
            int size
    );

    void createHistory(
            String month,
            String fileName,
            String filePath,
            Long createdById
    );
}
