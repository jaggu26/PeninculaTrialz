package com.peninsula.batch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peninsula.batch.model.AssignToBatch;
import com.peninsula.batch.model.BatchDetails;
import com.peninsula.registration.model.UserPersonalDetails;

public interface AssignToBatchRepository extends JpaRepository<AssignToBatch, Long> {
	// for player
	@Query(value = "select COUNT(a) > 0 from AssignToBatch a where a.userCredentials.userId = :playerId")
	boolean playerAlreadyAssignedOrNot(@Param("playerId") Long playerId);

	@Query(value = "select b from BatchDetails b where b.batchId = "
			+ "(select ab.batchDetails.batchId from AssignToBatch ab where ab.userCredentials.userId = :playerId)")
	BatchDetails findBatchDetailsByPlayerId(@Param("playerId") Long playerId);

	// for batch
	@Query(value = "select COUNT(a) > 0 from AssignToBatch a where a.batchDetails.batchId = :batchId")
	boolean batchHavePlayersOrNot(@Param("batchId") Long batchId);

	@Query("SELECT p FROM UserPersonalDetails p WHERE p.userCredentials.userId IN "
			+ "(SELECT ab.userCredentials.userId FROM AssignToBatch ab WHERE ab.batchDetails.batchId = :batchId)")
	List<UserPersonalDetails> findPlayerDetailsByBatchCode(@Param("batchId") Long batchId);
	@Query("SELECT p FROM UserPersonalDetails p WHERE p.userCredentials.userId IN "
			+ "(SELECT ab.userCredentials.userId FROM AssignToBatch ab WHERE ab.batchDetails.batchId = :batchId)")
	List<UserPersonalDetails> findCoachDetailsByBatchCode(@Param("batchId") Long batchId);

	@Query(value = "select COUNT(a) > 0 from AssignToBatch a where a.batchDetails.batchId = :batchId")
	boolean batchHaveCoachesOrNot(@Param("batchId") Long batchId);

	// for coach
	@Query(value = "select COUNT(a) > 0 from AssignToBatch a where a.userCredentials.userId = :coachId and a.batchDetails.batchId = :batchId")
	boolean coachAlreadyAssignedOrNotToSameBatch(Long coachId, Long batchId);

	@Query(value = "select COUNT(a) > 0 from AssignToBatch a where a.userCredentials.userId = :coachId")
	boolean coachAlreadyAssignedOrNot(@Param("coachId") Long coachId);

	@Query(value = "select b from BatchDetails b where b.batchId = "
			+ "(select ab.batchDetails.batchId from AssignToBatch ab where ab.userCredentials.username = :coachId)")
	List<BatchDetails> findBatchDetailsByCoachId(@Param("coachId") String coachId);

}
