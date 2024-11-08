package com.peninsula.event.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.peninsula.event.service.EventService;

@RestController
public class EventController {

	@Autowired
	private EventService eventService;

	Map<String, Object> response = new HashMap<>();

	@PostMapping("/createEvent")
	public ResponseEntity<Map<String, Object>> createEvent(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {

		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters and token required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = eventService.createEvent(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);
			}
		}

	}

	@GetMapping("/getEventByDate")
	public ResponseEntity<Map<String, Object>> getEventByDate(@RequestBody Map<String, Object> data,
			@RequestHeader("userToken") String userToken) {

		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters and token required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			// get the data as list from the service
			response = eventService.getEventByDate(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	@PostMapping("/updateEvent")
	public ResponseEntity<Map<String, Object>> updateEvent(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters and token required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = eventService.updateEvent(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);
			}

		}
	}
}
