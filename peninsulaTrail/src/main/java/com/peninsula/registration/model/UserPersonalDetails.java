package com.peninsula.registration.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Entity
public class UserPersonalDetails {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long personalId;
	
	@Column
	private String fullName;
	
	@Email(message = "Email should be in the correct format")
	private String email;
	
	@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be in the correct format")
	private String phoneNumber;
	
	@Column
	private String status;
	
	@Column
	private String gender;
	
//	@Column
//	private Long userId;
	@OneToOne
	@JoinColumn(name="userId", referencedColumnName = "userId")
	private UserCredentials userCredentials;
	
	public UserCredentials getUserCredentials() {
		return userCredentials;
	}

	public void setUserCredentials(UserCredentials userCredentials) {
		this.userCredentials = userCredentials;
	}

	@CreationTimestamp
    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;
	

	@UpdateTimestamp
	@Column(name = "updated_time")
	private LocalDateTime updatedTime;

//	public Long getUserId() {
//		return userId;
//	}
//
//	public void setUserId(Long userId) {
//		this.userId = userId;
//	}
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

	public Long getPersonalId() {
		return personalId;
	}

	public void setPersonalId(Long personalId) {
		this.personalId = personalId;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
