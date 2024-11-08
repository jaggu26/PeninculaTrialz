package com.peninsula.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peninsula.registration.model.UserPersonalDetails;

public interface RegistrationRepositoryForPersonalDetails extends JpaRepository<UserPersonalDetails, Long> {
	
	@Query(value = "Select COUNT(u) > 0 from UserPersonalDetails u where u.email = :email")
	boolean findEmailExistOrNot(@Param("email") String email);
	
}
