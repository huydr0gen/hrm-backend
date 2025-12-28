package com.tlu.hrm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.tlu.hrm.enums.AttendanceWorkType;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_records",
   uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "work_date"}))
public class AttendanceRecord {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    private LocalTime checkIn;
    private LocalTime checkOut;

    private Integer workedMinutes;
    private Integer paidMinutes;

    @Enumerated(EnumType.STRING)
    private AttendanceWorkType workType;

    private String note;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public AttendanceRecord() {
		super();
	}

	public AttendanceRecord(Long id, Employee employee, LocalDate workDate, LocalTime checkIn, LocalTime checkOut,
			Integer workedMinutes, Integer paidMinutes, AttendanceWorkType workType, String note) {
		super();
		this.id = id;
		this.employee = employee;
		this.workDate = workDate;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.workedMinutes = workedMinutes;
		this.paidMinutes = paidMinutes;
		this.workType = workType;
		this.note = note;
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

	public LocalDate getWorkDate() {
		return workDate;
	}

	public void setWorkDate(LocalDate workDate) {
		this.workDate = workDate;
	}

	public LocalTime getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(LocalTime checkIn) {
		this.checkIn = checkIn;
	}

	public LocalTime getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(LocalTime checkOut) {
		this.checkOut = checkOut;
	}

	public Integer getWorkedMinutes() {
		return workedMinutes;
	}

	public void setWorkedMinutes(Integer workedMinutes) {
		this.workedMinutes = workedMinutes;
	}

	public Integer getPaidMinutes() {
		return paidMinutes;
	}

	public void setPaidMinutes(Integer paidMinutes) {
		this.paidMinutes = paidMinutes;
	}

	public AttendanceWorkType getWorkType() {
		return workType;
	}

	public void setWorkType(AttendanceWorkType workType) {
		this.workType = workType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
    
    
    
}
