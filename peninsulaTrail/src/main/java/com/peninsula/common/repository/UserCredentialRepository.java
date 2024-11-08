package com.peninsula.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peninsula.registration.model.UserCredentials;

public interface UserCredentialRepository extends JpaRepository<UserCredentials, Long>{
	
	
	@Query(value="select u from UserCredentials u where u.userId = :playerId and u.role = 'player'")
	UserCredentials getUserIdByPlayerId(@Param("playerId") Long playerId);
	
	@Query(value="select u from UserCredentials u where u.userId = :coachId and u.role = 'coach'")
	UserCredentials getUserIdByCoachId(@Param("coachId") Long coachId);
	
	@Query(value="select u from UserCredentials u where u.userId = :userId")
	UserCredentials findByUserId(Long userId);
	
	@Query(value="select u from UserCredentials u where u.username = :playerId and u.role = 'player'")
	UserCredentials getUserIdByPlayer(@Param("playerId") String playerId);

	
}
