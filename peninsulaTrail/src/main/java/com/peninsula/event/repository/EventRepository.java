package com.peninsula.event.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.peninsula.event.model.EventDetails;

public interface EventRepository extends JpaRepository<EventDetails, Long>{
	
	
	// checking if any events are present in the particular date and returns boolean( true / false
	@Query(value="select COUNT(e) > 0 from EventDetails e where e.eventDate = :eventDate")
	boolean findByEventDate(@Param("eventDate")LocalDate eventDate);
	
	
	//getting the list of events on the particular date and return the list
	@Query(value="select e from EventDetails e where e.eventDate = :eventDate")
	List<EventDetails> findEventsByDate(@Param("eventDate") LocalDate eventDate);

	@Query(value="select e from EventDetails e where e.eventId = :eventId")
	EventDetails findByEventId(Long eventId);

}
