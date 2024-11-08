package com.peninsula.batch.controller;

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

import com.peninsula.batch.service.BatchService;

@RestController
public class BatchController {

	@Autowired
	private BatchService batchService;

	Map<String, Object> response = new HashMap<>();

	@PostMapping("/createBatch")
	public ResponseEntity<Map<String, Object>> createBatch(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.addBatch(data, userToken);

			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	// player and batch controllers-->

	@PostMapping("/assignPlayerToBatch")
	public ResponseEntity<Map<String, Object>> assignPlayerToBatch(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.assignPlayerToBatch(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	@GetMapping("/getBatchOfaPlayer")
	public ResponseEntity<Map<String, Object>> getBatchOfaPlayer(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.getBatchOfaPlayer(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response); 

			}
		}
	}

	@GetMapping("/getPlayersFromaBatch")
	public ResponseEntity<Map<String, Object>> getPlayersFromaBatch(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {

			response = batchService.getPlayersFromaBatch(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	// coach and batch controllers--->
	@PostMapping("/assignCoachToBatch")
	public ResponseEntity<Map<String, Object>> assignCoachToBatch(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.assignCoachToBatch(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	@GetMapping("/getBatchOfaCoach")
	public ResponseEntity<Map<String, Object>> getBatchOfaCoach(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.getBatchOfaCoach(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

	@GetMapping("/getCoachesFromaBatch")
	public ResponseEntity<Map<String, Object>> getsCoachesFromaBatch(@RequestBody HashMap<String, Object> data,
			@RequestHeader("userToken") String userToken) {
		response.clear();
		if (data.get("parameters") == null || data.get("userCredentials") == null || userToken == null
				|| userToken.isEmpty()) {
			response.put("status", "Error");
			response.put("message", "all parameters required");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

		} else {
			response = batchService.getCoachesFromaBatch(data, userToken);
			if (response.get("status").toString().equals("Error")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
			} else {
				return ResponseEntity.ok(response);

			}
		}
	}

}
