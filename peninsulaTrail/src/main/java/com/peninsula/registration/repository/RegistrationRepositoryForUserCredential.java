package com.peninsula.registration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peninsula.registration.model.UserCredentials;

public interface RegistrationRepositoryForUserCredential extends JpaRepository<UserCredentials, Long> {
	
	
	@Query(value="select u.userId from UserCredentials u where u.username = :email")
	Long findUserIdByEmail(String email);

}
