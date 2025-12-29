package com.tlu.hrm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.tlu.hrm.dto.OTParticipantDTO;
import com.tlu.hrm.dto.OTRequestCreateDTO;
import com.tlu.hrm.dto.OTRequestResponseDTO;
import com.tlu.hrm.dto.OTResponseDTO;
import com.tlu.hrm.entities.Employee;
import com.tlu.hrm.entities.OTParticipant;
import com.tlu.hrm.entities.OTRequest;
import com.tlu.hrm.enums.OTParticipantStatus;
import com.tlu.hrm.enums.OTRequestStatus;
import com.tlu.hrm.repository.EmployeeRepository;
import com.tlu.hrm.repository.OTParticipantRepository;
import com.tlu.hrm.repository.OTRequestRepository;
import com.tlu.hrm.security.CustomUserDetails;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class OTServiceImpl implements OTService {

	private final OTRequestRepository otRepo;
    private final OTParticipantRepository participantRepo;
    private final EmployeeRepository employeeRepo;
    
	public OTServiceImpl(OTRequestRepository otRepo, OTParticipantRepository participantRepo,
			EmployeeRepository employeeRepo) {
		super();
		this.otRepo = otRepo;
		this.participantRepo = participantRepo;
		this.employeeRepo = employeeRepo;
	}
    
	@Override
	@Transactional
	public OTRequestResponseDTO createOT(OTRequestCreateDTO dto) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null || !auth.isAuthenticated()) {
	        throw new RuntimeException("Unauthenticated");
	    }

	    Object principal = auth.getPrincipal();
	    if (!(principal instanceof CustomUserDetails ud)) {
	        throw new RuntimeException("Cannot resolve current user");
	    }

	    Employee manager = employeeRepo.findByUserId(ud.getId())
	            .orElseThrow(() -> new RuntimeException("Employee not found"));

	    OTRequest ot = new OTRequest();
	    ot.setOtDate(dto.getOtDate());
	    ot.setStartTime(dto.getStartTime());
	    ot.setEndTime(dto.getEndTime());
	    ot.setReason(dto.getReason());
	    ot.setManager(manager);

	    List<OTParticipant> participants = dto.getEmployeeCodes().stream()
	            .map(code -> {

	                Employee emp = employeeRepo.findByCode(code)
	                        .orElseThrow(() ->
	                                new RuntimeException("Employee not found: " + code)
	                        );

	                if (!emp.getDepartment().getId()
	                        .equals(manager.getDepartment().getId())) {
	                    throw new IllegalArgumentException(
	                            "Employee not in manager department: " + code
	                    );
	                }

	                OTParticipant p = new OTParticipant();
	                p.setEmployee(emp);
	                p.setOtRequest(ot);
	                return p;
	            })
	            .toList();

	    ot.setParticipants(participants);

	    OTRequest saved = otRepo.save(ot);

	    return toDTO(saved);
	}

    @Override
    public void respondOT(Long participantId, OTResponseDTO dto) {

        OTParticipant p = participantRepo.findById(participantId)
                .orElseThrow();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        Employee emp = employeeRepo.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!p.getEmployee().getId().equals(emp.getId())) {
            throw new SecurityException("You can only respond to your own OT request");
        }
        
        if (dto.isAccept()) {
            p.setStatus(OTParticipantStatus.ACCEPTED);
        } else {
            p.setStatus(OTParticipantStatus.REJECTED);
            p.setRejectReason(dto.getRejectReason());
        }

        p.setRespondedAt(LocalDateTime.now());

        updateRequestStatus(p.getOtRequest());
    }

    private void updateRequestStatus(OTRequest ot) {

    	boolean anyAccepted = ot.getParticipants()
                .stream()
                .anyMatch(p -> p.getStatus() == OTParticipantStatus.ACCEPTED);

        boolean allAccepted = ot.getParticipants()
                .stream()
                .allMatch(p -> p.getStatus() == OTParticipantStatus.ACCEPTED);

        boolean allRejected = ot.getParticipants()
                .stream()
                .allMatch(p -> p.getStatus() == OTParticipantStatus.REJECTED);

        if (allAccepted) {
            ot.setStatus(OTRequestStatus.ACCEPTED);
        } else if (allRejected) {
            ot.setStatus(OTRequestStatus.REJECTED);
        } else if (anyAccepted) {
            ot.setStatus(OTRequestStatus.PARTIALLY_ACCEPTED);
        } else {
            ot.setStatus(OTRequestStatus.PENDING);
        }
    }

    @Override
    public Page<OTRequestResponseDTO> getMyOTs(int page, int size) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        Employee emp = employeeRepo.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("otRequest.createdAt").descending()
        );

        return participantRepo
                .findByEmployeeId(emp.getId(), pageable)
                .map(this::toDTOForEmployee);
    }
    
    @Override
    public Page<OTRequestResponseDTO> getManagerOTs(
            OTRequestStatus status,
            int page,
            int size
    ) {

    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth == null || !auth.isAuthenticated()) {
    	    throw new RuntimeException("Unauthenticated");
    	}

    	CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        Employee manager = employeeRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending()
        );

        Page<OTRequest> result;

        if (status != null) {
            result = otRepo.findByManagerIdAndStatus(
                    manager.getId(), status, pageable
            );
        } else {
            result = otRepo.findByManagerId(
                    manager.getId(), pageable
            );
        }

        return result.map(this::toDTO);
    }

    @Override
    public void cancelOT(Long otRequestId) {

        OTRequest ot = otRepo.findById(otRequestId).orElseThrow();
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails ud = (CustomUserDetails) auth.getPrincipal();

        Employee manager = employeeRepo.findByUserId(ud.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (!ot.getManager().getId().equals(manager.getId())) {
            throw new SecurityException("You can only cancel your own OT request");
        }

        if (ot.getStatus() != OTRequestStatus.PENDING) {
            throw new RuntimeException("Cannot cancel");
        }

        ot.setStatus(OTRequestStatus.CANCELLED);
    }
    
	 // =====================================================
	 // DTO Mapper
	 // =====================================================
	 private OTRequestResponseDTO toDTO(OTRequest ot) {
	
	     OTRequestResponseDTO dto = new OTRequestResponseDTO();
	
	     dto.setId(ot.getId());
	     dto.setOtDate(ot.getOtDate());
	     dto.setStartTime(ot.getStartTime());
	     dto.setEndTime(ot.getEndTime());
	     dto.setReason(ot.getReason());
	     dto.setStatus(ot.getStatus());
	
	     // manager info
	     if (ot.getManager() != null) {
	         dto.setManagerId(ot.getManager().getId());
	         dto.setManagerName(ot.getManager().getFullName());
	     }
	
	     // participants
	     List<OTParticipantDTO> participantDTOs = new ArrayList<>();
	     if (ot.getParticipants() != null) {
	         for (OTParticipant p : ot.getParticipants()) {
	
	             OTParticipantDTO pDto = new OTParticipantDTO();
	             pDto.setEmployeeId(p.getEmployee().getId());
	             pDto.setEmployeeCode(p.getEmployee().getCode());
	             pDto.setEmployeeName(p.getEmployee().getFullName());
	             pDto.setStatus(p.getStatus());
	             pDto.setRejectReason(p.getRejectReason());
	             pDto.setRespondedAt(p.getRespondedAt());
	
	             participantDTOs.add(pDto);
	         }
	     }
	
	     dto.setParticipants(participantDTOs);
	     dto.setCreatedAt(ot.getCreatedAt());
	
	     return dto;
	 }
	 
	 private OTRequestResponseDTO toDTOForEmployee(OTParticipant p) {

		    OTRequest ot = p.getOtRequest();

		    OTRequestResponseDTO dto = new OTRequestResponseDTO();

		    dto.setId(ot.getId());
		    dto.setOtDate(ot.getOtDate());
		    dto.setStartTime(ot.getStartTime());
		    dto.setEndTime(ot.getEndTime());
		    dto.setReason(ot.getReason());
		    dto.setStatus(ot.getStatus());

		    // manager info
		    dto.setManagerId(ot.getManager().getId());
		    dto.setManagerName(ot.getManager().getFullName());

		    // CHỈ TRẢ VỀ PARTICIPANT CỦA CHÍNH NHÂN VIÊN
		    OTParticipantDTO pDto = new OTParticipantDTO();
		    pDto.setId(p.getId());
		    pDto.setEmployeeId(p.getEmployee().getId());
		    pDto.setEmployeeCode(p.getEmployee().getCode());
		    pDto.setEmployeeName(p.getEmployee().getFullName());
		    pDto.setStatus(p.getStatus());
		    pDto.setRejectReason(p.getRejectReason());
		    pDto.setRespondedAt(p.getRespondedAt());

		    dto.setParticipants(List.of(pDto));
		    dto.setCreatedAt(ot.getCreatedAt());

		    return dto;
		}
    
}
