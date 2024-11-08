package com.peninsula.user.management.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.peninsula.user.management.service.UserManagementService;

@RestController
public class UserManagementController {

	@Autowired
	public UserManagementService userManagementService;

	@PostMapping("/userBlocking")
	public ResponseEntity<Map<String, Object>> userBlocking(@RequestBody Map<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters and token required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} else {
			response = userManagementService.userBlocking(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);
			}
		}
	}

}
