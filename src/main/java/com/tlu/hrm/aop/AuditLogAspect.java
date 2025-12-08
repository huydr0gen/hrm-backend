package com.tlu.hrm.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tlu.hrm.service.AuditLogService;

public class AuditLogAspect {

	private final AuditLogService auditLogService;

	public AuditLogAspect(AuditLogService auditLogService) {
		super();
		this.auditLogService = auditLogService;
	}
	
	@AfterReturning("execution(* com.tlu.hrm.controller..*(..))")
    public void logControllerActions(JoinPoint joinPoint) {

        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "anonymous";

        String action = joinPoint.getSignature().getName();
        String details = "Called API: " + joinPoint.getSignature().toShortString();

        auditLogService.log(
                null,
                action,
                details
        );
    }
}
