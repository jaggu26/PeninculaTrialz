package com.peninsula.registration.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.peninsula.registration.service.RegistrationService;

@RestController
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@PostMapping("/registration")
	public ResponseEntity<Map<String, Object>> getRegistrationDetails(@RequestBody HashMap<String, Object> data) {
		Map<String, Object> response = new HashMap<>();
		if (data.get("email") == null || data.get("email").toString().isEmpty() || data.get("fullName") == null
				|| data.get("fullName").toString().isEmpty() || data.get("phoneNumber") == null
				|| data.get("phoneNumber").toString().isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} else {
			response = registrationService.saveRegistrationDetails(data);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);
			}
		}

	}

}
