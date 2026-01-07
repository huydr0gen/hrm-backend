package com.tlu.hrm.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "salary_import_histories")
public class SalaryImportHistory {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tháng import, ví dụ: 2026-01
    @Column(nullable = false)
    private String month;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    // Có thể là path local hoặc url
    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Employee createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

	public SalaryImportHistory() {
		super();
	}

	public SalaryImportHistory(Long id, String month, String fileName, String filePath, Employee createdBy,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.month = month;
		this.fileName = fileName;
		this.filePath = filePath;
		this.createdBy = createdBy;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    
}
