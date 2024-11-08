package com.peninsula.user.management.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peninsula.common.repository.UserCredentialRepository;
import com.peninsula.communication.email.model.EmailRequest;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.service.RegistrationService;

@Service
public class UserManagementService {

	@Autowired
	public UserCredentialRepository userCredentialRepository;

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	RestTemplate restTemplate = new RestTemplate();
	private static final String EmailAPI = "http://localhost:8080/send-email";

	public Map<String, Object> userBlocking(Map<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("userId") == null || parameters.get("userId").toString().isEmpty()
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
			Long userId = Long.parseLong(parameters.get("userId").toString().trim());

			// Check if the user exists by finding the user personal details with the given
			// email
			UserCredentials blockUserCredentials = userCredentialRepository.findByUserId(userId);
			if (blockUserCredentials == null) {

				response.put("status", "Error");
				response.put("message", "User does not exist");
				return response;

			} else if (blockUserCredentials.getUserPersonalDetails().getStatus().equals("blocked")) {
				response.put("status", "Error");
				response.put("message", "This user is already blocked");
				return response;
			} else {
				// If user exists, fetch personal details and update status to 'blocked'

				blockUserCredentials.getUserPersonalDetails().setStatus("blocked");
				userCredentialRepository.save(blockUserCredentials);

				// Notify user via email about account block
				sendBlockEmail(blockUserCredentials.getUserPersonalDetails().getEmail(),
						blockUserCredentials.getUserPersonalDetails().getFullName());

				// Response indicating successful block
				response.put("status", "Success");
				response.put("message", "Account blocked successfully");
				return response;
			}

		} catch (Exception e) {
			logger.info("There is an error", e);
			response.put("status", "Error");
			return response;
		}

	}

	// for sending email
	private void sendBlockEmail(String email, String fullName) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(email);
		emailRequest.setSubject("Regret to inform !");
		EmailRequest.Body body = new EmailRequest.Body();
		body.setGreeting("Dear, " + fullName + "!");
		body.setMain("Your account is blocked");
		body.setFooter("Best Regards,Company");
		emailRequest.setBody(body);
		restTemplate.postForObject(EmailAPI, emailRequest, Void.class);
	}

}
