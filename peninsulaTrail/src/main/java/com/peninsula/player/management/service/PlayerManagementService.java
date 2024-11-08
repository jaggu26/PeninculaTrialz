package com.peninsula.player.management.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
public class PlayerManagementService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	public UserCredentialRepository userCredentialRepository;

	RestTemplate restTemplate = new RestTemplate();
	private static final String EmailAPI = "http://localhost:8080/send-email";

	public Map<String, Object> playerApproval(Map<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();

		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (parameters.get("approvalStatus") == null || parameters.get("approvalStatus").toString().isEmpty()
					|| parameters.get("userId") == null || parameters.get("userId").toString().isEmpty()
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

			// Extract status and email from the input data
			String approvalStatus = parameters.get("approvalStatus").toString().trim();
			Long userId = Long.parseLong(parameters.get("userId").toString().trim());
			UserCredentials playerApprovalUserDetails = userCredentialRepository.findByUserId(userId);

			if (playerApprovalUserDetails == null) {
				// Response for case when user credentials are not found
				response.put("status", "Error");
				response.put("message", "The user doesn't  exist");
				return response;

			} else if (playerApprovalUserDetails.getUserPersonalDetails().getStatus().equals("approved")
					|| playerApprovalUserDetails.getUserPersonalDetails().getStatus().equals("rejected")) {
				response.put("status", "Error");
				response.put("message", "This user already approved/rejected can't perform twice");
				return response;
			} else {

				// Handling approval request
				if (!"approved".equals(approvalStatus) && !"rejected".equals(approvalStatus)) {
					response.put("status", "Error");
					response.put("message", "invalid request");
					return response;
				}
				// Handling rejection request
				else if ("approved".equals(approvalStatus)) {
					String playerId = generatePlayerId();
					playerApprovalUserDetails.setUsername(playerId);
					playerApprovalUserDetails.setRole("player");
					userCredentialRepository.save(playerApprovalUserDetails);

					// Update user status to approved
					playerApprovalUserDetails.getUserPersonalDetails().setStatus("approved");
					userCredentialRepository.save(playerApprovalUserDetails);

					// Sent approval email with player id
					sendApproval(playerApprovalUserDetails.getUserPersonalDetails().getEmail(), playerId,
							playerApprovalUserDetails.getUserPersonalDetails().getFullName());

					response.put("status", "Success");
					response.put("message", "Your Request Has Been Approved");

					return response;
				} else {
					// Update user status to rejected
					playerApprovalUserDetails.getUserPersonalDetails().setStatus("rejected");
					userCredentialRepository.save(playerApprovalUserDetails);

					// Send rejection email
					sendRejection(playerApprovalUserDetails.getUserPersonalDetails().getEmail(),
							playerApprovalUserDetails.getUserPersonalDetails().getFullName());

					response.put("status", "Success");
					response.put("message", "SORRY, Your request has been rejected");
					return response;
				}

			}

		}
		// Log any exceptions that occur during processing
		catch (Exception e) {
			logger.info("There is an error", e);
			response.put("status", "Error");
			return response;

		}

	}

	// for sending email
	private void sendApproval(String email, String newPlayerId, String fullName) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(email);
		emailRequest.setSubject("Welcome to our Peninsula Community!");
		EmailRequest.Body body = new EmailRequest.Body();
		body.setGreeting("Dear, " + fullName + "!");
		body.setMain("Thank you for registering with us. We are glad to have you on board. Your new player id is  "
				+ newPlayerId);
		body.setFooter("Best Regards,Company");
		emailRequest.setBody(body);
		restTemplate.postForObject(EmailAPI, emailRequest, Void.class);
	}

	private void sendRejection(String email, String fullName) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(email);
		emailRequest.setSubject("About Your Application !");
		EmailRequest.Body body = new EmailRequest.Body();
		body.setGreeting("Dear, " + fullName + "!");
		body.setMain(
				"we looked through you application unfortunatily some requirement is not met, please try again later");
		body.setFooter("Best Regards,Company");
		emailRequest.setBody(body);
		restTemplate.postForObject(EmailAPI, emailRequest, Void.class);
	}

	// Generates a unique player ID
	public static String generatePlayerId() {
		String prefix = "PEN";
		// Get the last two digits of the current year
		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		String lastTwoDigitsOfYear = year.substring(year.length() - 2);
		String suffix = "PID";

		// Generates a random 3-digit number
		int randomNumber = new Random().nextInt(900) + 100; // 100 to 999

		return prefix + lastTwoDigitsOfYear + suffix + randomNumber;
	}

}
