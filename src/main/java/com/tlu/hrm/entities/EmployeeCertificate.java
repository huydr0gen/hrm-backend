package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "employee_certificates")
public class EmployeeCertificate {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATION =====
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    // ===== INFO =====
    @Column(nullable = false)
    private String name;        // Tên bằng cấp

    private String issuer;      // Đơn vị cấp

    private LocalDate issuedDate;

    private LocalDate expiredDate;

    @Column(length = 500)
    private String note;

    // ===== AUDIT =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public EmployeeCertificate() {
		super();
	}

	public EmployeeCertificate(Long id, Employee employee, String name, String issuer, LocalDate issuedDate,
			LocalDate expiredDate, String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.employee = employee;
		this.name = name;
		this.issuer = issuer;
		this.issuedDate = issuedDate;
		this.expiredDate = expiredDate;
		this.note = note;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public LocalDate getIssuedDate() {
		return issuedDate;
	}

	public void setIssuedDate(LocalDate issuedDate) {
		this.issuedDate = issuedDate;
	}

	public LocalDate getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(LocalDate expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
