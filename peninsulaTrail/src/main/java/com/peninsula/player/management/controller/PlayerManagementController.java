package com.peninsula.player.management.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.peninsula.player.management.service.PlayerManagementService;

@RestController
public class PlayerManagementController {

	@Autowired
	public PlayerManagementService playerManagementService;

	@PostMapping("/playerApprovalStatusRequest")
	public ResponseEntity<Map<String, Object>> playerManagement(@RequestBody Map<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		Map<String, Object> response = new HashMap<>();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters and token required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		} else {
			response = playerManagementService.playerApproval(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);
			}
		}
	}

}
