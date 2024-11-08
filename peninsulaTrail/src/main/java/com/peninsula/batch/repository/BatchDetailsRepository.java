package com.peninsula.batch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peninsula.batch.model.BatchDetails;

public interface BatchDetailsRepository extends JpaRepository<BatchDetails, Long> {
	
	@Query(value="select COUNT(b) > 0 from BatchDetails b where b.batchName = :batchName")
	boolean batchNameExistOrNot(String batchName);

	
	@Query(value="select b from BatchDetails b where b.batchId = :batchId")
	BatchDetails getBatchByBatchId(Long batchId);

	@Query(value="select b from BatchDetails b where b.batchCode = :batchCode")
	BatchDetails getBatchByBatchCode(String batchCode);

}

