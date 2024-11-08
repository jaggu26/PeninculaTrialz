package com.peninsula.event.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peninsula.common.repository.UserCredentialRepository;
import com.peninsula.event.model.EventDetails;
import com.peninsula.event.repository.EventRepository;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.service.RegistrationService;

@Service
public class EventService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	private EventRepository eventRepository;

	@Autowired
	private UserCredentialRepository userCredentialRepository;

	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

	// method to create a new event
	public HashMap<String, Object> createEvent(HashMap<String, Object> data, String userToken) {
		HashMap<String, Object> response = new HashMap<>();

		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> event = (HashMap<String, Object>) parameters.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (event.get("eventName") == null || event.get("eventName").toString().isEmpty()
					|| event.get("eventDate") == null || event.get("eventDate").toString().isEmpty()
					|| event.get("eventTime") == null || event.get("eventTime").toString().isEmpty()
					|| event.get("eventVenue") == null || event.get("eventVenue").toString().isEmpty()
					|| event.get("eventType") == null || event.get("eventType").toString().isEmpty()
					|| event.get("eventLink") == null || event.get("eventLink").toString().isEmpty()
					|| userCredentials.get("userId") == null || userCredentials.get("userId").toString().isEmpty()
					|| userToken == null || userToken.isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// validating user and token
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}
			// getting all the parameters from request
			String eventName = event.get("eventName").toString().trim();
			String eventDateStr = event.get("eventDate").toString().trim();
			String eventTimeStr = event.get("eventTime").toString().trim();
			String eventVenue = event.get("eventVenue").toString().trim();
			String eventType = event.get("eventType").toString().trim();
			String eventLink = event.get("eventLink").toString().trim();

			// checking the time and date coming in the correct format

			try {
				LocalDate eventDate = LocalDate.parse(eventDateStr, dateFormatter);
				LocalTime eventTime = LocalTime.parse(eventTimeStr, timeFormatter);
				EventDetails eventDetails = new EventDetails();
				eventDetails.setEventName(eventName);
				eventDetails.setEventDate(eventDate);
				eventDetails.setEventTime(eventTime);
				eventDetails.setEventVenue(eventVenue);
				eventDetails.setEventType(eventType);
				eventDetails.setEventLink(eventLink);

				// saving the table with data
				eventRepository.save(eventDetails);

				response.put("eventVenue", eventVenue);
				response.put("eventType", eventType);
				response.put("eventLink", eventLink);
				response.put("eventTime", eventTime);
				response.put("eventDate", eventDate);
				response.put("eventName", eventName);
				response.put("status", "Success");
				response.put("message", "Event created successfully");

				return response;

			} catch (DateTimeParseException e) {
				logger.info("Error occured", e);
				response.put("status", "Error");
				response.put("message", "inavlid date/time format, dd-mm-yyyy/h:mm a use this");
				return response;
			}
			// setting all the values in the corresponding fields of the table
		} catch (Exception e) {

			logger.info("There is an error", e);
			response.put("status", "Error");
			response.put("message", "Exception error occured");
			return response;

		}
	}

	// method to get the events of a day
	public Map<String, Object> getEventByDate(Map<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> event = (HashMap<String, Object>) parameters.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (event.get("eventDate") == null || event.get("eventDate").toString().isEmpty()
					|| userCredentials.get("userId") == null || userCredentials.get("userId").toString().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;
			}
			// validating user and token
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			String eventDateStr = event.get("eventDate").toString().trim();

			try {
				LocalDate eventDate = LocalDate.parse(eventDateStr, dateFormatter);
				boolean isEventExist = eventRepository.findByEventDate(eventDate);
				// checking if any event exist in this date or not
				if (!isEventExist) // if not exist this will execute
				{
					response.put("status", "Error");
					response.put("message", "No events in selected date");
					return response;
				}
				// if the event exist in this date, this part will execute
				else {
					// get the list of events from the tables and return as list to controller
					List<EventDetails> eventsDetails = eventRepository.findEventsByDate(eventDate);
					for (EventDetails eventDetail : eventsDetails) {
						Map<String, Object> eventDetailsResponse = new HashMap<>();
						eventDetailsResponse.put("eventDate", eventDetail.getEventDate());
						eventDetailsResponse.put("eventTime", eventDetail.getEventTime());
						eventDetailsResponse.put("eventVenue", eventDetail.getEventVenue());
						eventDetailsResponse.put("eventType", eventDetail.getEventType());
						eventDetailsResponse.put("eventLink", eventDetail.getEventLink());
						response.put(eventDetail.getEventName(), eventDetailsResponse);
					}

					response.put("status", "Success");
					response.put("message", "Event details fetched successfully");
					return response;
				}
			} catch (DateTimeParseException e) {
				logger.info("Error occured", e);
				response.put("status", "Error");
				response.put("message", "inavlid date/time format, dd-mm-yyyy/h:mm a use this");
				return response;
			}
		} catch (Exception e) {

			logger.info("Error occured", e);
			response.put("status", "Error");
			response.put("message", "Exception error occured");
			return response;
		}
	}

	// updating event
	public HashMap<String, Object> updateEvent(HashMap<String, Object> data, String userToken) {
		HashMap<String, Object> response = new HashMap<>();

		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> event = (HashMap<String, Object>) parameters.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (event.get("eventName") == null || event.get("eventName").toString().isEmpty()
					|| event.get("eventDate") == null || event.get("eventDate").toString().isEmpty()
					|| event.get("eventTime") == null || event.get("eventTime").toString().isEmpty()
					|| event.get("eventVenue") == null || event.get("eventVenue").toString().isEmpty()
					|| event.get("eventType") == null || event.get("eventType").toString().isEmpty()
					|| event.get("eventLink") == null || event.get("eventLink").toString().isEmpty()
					|| event.get("eventId") == null || event.get("eventId").toString().isEmpty()
					|| userCredentials.get("userId") == null || userCredentials.get("userId").toString().isEmpty()) {
				response.put("status", "Error");
				response.put("message", "all parameters required");
				return response;

			}
			// validating user and token
			UserCredentials user = userCredentialRepository
					.findByUserId(Long.parseLong(userCredentials.get("userId").toString().trim()));
			boolean token = Boolean.parseBoolean(userToken);
			if (user == null) {
				response.put("status", "Error");
				response.put("message", "User doesn't exist");
				return response;
			} else if (!token) {
				response.put("status", "Error");
				response.put("message", "invalid token");
				return response;
			}

			// getting all the parameters from request
			Long eventId = Long.parseLong(event.get("eventId").toString().trim());
			String eventName = event.get("eventName").toString().trim();
			String eventDateStr = event.get("eventDate").toString().trim();
			String eventTimeStr = event.get("eventTime").toString().trim();
			String eventVenue = event.get("eventVenue").toString().trim();
			String eventType = event.get("eventType").toString().trim();
			String eventLink = event.get("eventLink").toString().trim();

			EventDetails eventDetails = eventRepository.findByEventId(eventId);

			if (eventDetails == null) {
				response.put("status", "Error");
				response.put("message", "invalid event id");
				return response;
			} else {
				// setting all the values in the corresponding fields of the table
				try {
					LocalDate eventDate = LocalDate.parse(eventDateStr, dateFormatter);
					LocalTime eventTime = LocalTime.parse(eventTimeStr, timeFormatter);
					eventDetails.setEventName(eventName);
					eventDetails.setEventDate(eventDate);
					eventDetails.setEventTime(eventTime);
					eventDetails.setEventVenue(eventVenue);
					eventDetails.setEventType(eventType);
					eventDetails.setEventLink(eventLink);

					// updating event in the table with new data
					eventRepository.save(eventDetails);
					response.put("eventVenue", eventVenue);
					response.put("eventType", eventType);
					response.put("eventLink", eventLink);
					response.put("eventTime", eventTime);
					response.put("eventDate", eventDate);
					response.put("eventName", eventName);
					response.put("status", "Success");
					response.put("message", "Event updated successfully");

					return response;
				} catch (DateTimeParseException e) {
					logger.info("Error occured", e);
					response.put("status", "Error");
					response.put("message", "inavlid date/time format, dd-mm-yyyy/h:mm a use this");
					return response;
				}

			}

		} catch (Exception e) {

			logger.info("An error occured", e);
			response.put("status", "Error");
			response.put("message", "Exception error occured");
			return response;
		}

	}
}
