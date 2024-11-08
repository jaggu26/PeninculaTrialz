package com.peninsula.batch.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.peninsula.registration.model.UserCredentials;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AssignToBatch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignToBatchId;
	

    @ManyToOne
    @JoinColumn(name = "batchId", referencedColumnName = "batchId")
    private BatchDetails batchDetails;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private UserCredentials userCredentials;
    
    public Long getAssignToBatchId() {
		return assignToBatchId;
	}

	public void setAssignToBatchId(Long assignToBatchId) {
		this.assignToBatchId = assignToBatchId;
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

	@CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

	@UpdateTimestamp
	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

	public Long getAssignPlayersToBatchId() {
		return assignToBatchId;
	}

	public void setAssignPlayersToBatchId(Long assignPlayersToBatchId) {
		this.assignToBatchId = assignPlayersToBatchId;
	}

	public BatchDetails getBatchDetails() {
		return batchDetails;
	}

	public void setBatchDetails(BatchDetails batchDetails) {
		this.batchDetails = batchDetails;
	}

	public UserCredentials getUserCredentials() {
		return userCredentials;
	}

	public void setUserCredentials(UserCredentials userCredentials) {
		this.userCredentials = userCredentials;
	}
	
	
	
}
