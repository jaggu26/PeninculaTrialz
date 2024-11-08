package com.peninsula.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peninsula.registration.model.UserPersonalDetails;

public interface UserPersonalDetailsRepository extends JpaRepository<UserPersonalDetails, Long> {
	
	
	//checking email exist or not
	@Query(value = "Select COUNT(u) > 0 from UserPersonalDetails u where u.email = :email")
	boolean findEmailExistOrNot(String email);
	
	//checking and getting the user
	@Query(value="select u from UserPersonalDetails u where u.email = :email")
	UserPersonalDetails findByEmail(String email);
	



}
