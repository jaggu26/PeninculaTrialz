package com.peninsula.batch.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class BatchDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long batchId;
	
	@Column
	private String batchName;
	
	@Column
	private String batchType;
	
	@Column
	private LocalDate batchStartingDate;
	
	@Column
	private LocalDate batchEndingDate;
	
	@Column
	private String batchCode;
	
	@CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

	@UpdateTimestamp
	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

	public String getBatchCode() {
		return batchCode;
	}

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	public LocalDateTime getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(LocalDateTime updatedTime) {
		this.updatedTime = updatedTime;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public Long getBatchId() {
		return batchId;
	}

	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}

	public String getBatchName() {
		return batchName;
	}

	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public LocalDate getBatchStartingDate() {
		return batchStartingDate;
	}

	public void setBatchStartingDate(LocalDate batchStartingDate) {
		this.batchStartingDate = batchStartingDate;
	}

	public LocalDate getBatchEndingDate() {
		return batchEndingDate;
	}

	public void setBatchEndingDate(LocalDate batchEndingDate) {
		this.batchEndingDate = batchEndingDate;
	}
	
	
}
