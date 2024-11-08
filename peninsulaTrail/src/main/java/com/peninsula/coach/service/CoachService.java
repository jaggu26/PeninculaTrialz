package com.peninsula.coach.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peninsula.batch.service.BatchService;
import com.peninsula.common.repository.UserCredentialRepository;
import com.peninsula.common.repository.UserPersonalDetailsRepository;
import com.peninsula.common.utils.Utils;
import com.peninsula.communication.email.model.EmailRequest;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.model.UserPersonalDetails;
import com.peninsula.registration.service.RegistrationService;

@Service
public class CoachService {

	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

	@Autowired
	private UserPersonalDetailsRepository userPersonalDetailsRepository;

	@Autowired
	private UserCredentialRepository userCredentialRepository;

	@Autowired
	private BatchService batchService;

	RestTemplate restTemplate = new RestTemplate();
	private static final String EmailAPI = "http://localhost:8080/send-email";

	// for adding coach
	public Map<String, Object> addCoach(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			// checking all parameters coming or not
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> coach = (HashMap<String, Object>) parameters.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (coach.get("email") == null || coach.get("email").toString().isEmpty() || coach.get("fullName") == null
					|| coach.get("fullName").toString().isEmpty() || coach.get("phoneNumber") == null
					|| coach.get("phoneNumber").toString().isEmpty() || userCredentials.get("userId") == null
					|| userCredentials.get("userId").toString().isEmpty() || userToken == null || userToken.isEmpty()) {
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
			String email = coach.get("email").toString().trim();
			boolean isEmailExist = userPersonalDetailsRepository.findEmailExistOrNot(email);
			if (isEmailExist) {
				response.put("status", "Error");
				response.put("message", "This email is already exist");
				return response;
			} else {
				// saving to user credential table
				UserCredentials coachCredentials = new UserCredentials();
				// Generate a unique employee ID
				String uniqueEmployeeId = generateEmployeeId();
				// Set the unique ID as the userId
				coachCredentials.setUsername(uniqueEmployeeId);
				String generatedPassword = Utils.generatePassword();
				sendUserCredential(coach.get("email").toString().trim(), generatedPassword, uniqueEmployeeId,
						coach.get("fullName").toString().trim());// calling sending email
				coachCredentials.setPassword(Utils.hashPassword(generatedPassword));
				coachCredentials.setRole("coach");
				userCredentialRepository.save(coachCredentials);

				// saving to personal details table
				UserPersonalDetails userPersonalDetails = new UserPersonalDetails();
				userPersonalDetails.setEmail(coach.get("email").toString().trim());
				userPersonalDetails.setFullName(coach.get("fullName").toString().trim());
				userPersonalDetails.setPhoneNumber(coach.get("phoneNumber").toString().trim());
				userPersonalDetails.setUserCredentials(coachCredentials);
				coachCredentials.setUserPersonalDetails(userPersonalDetails);
				userPersonalDetailsRepository.save(userPersonalDetails);
				List<Map<String, Object>> batches = new ArrayList<>();
				List<Map<String, Object>> coaches = new ArrayList<>();
				if (data.get("batches") != null) {
					HashMap<String, Object> coachMap = new HashMap<>();
					coachMap.put("coachId", coachCredentials.getUserId());
					coaches.add(coachMap);
					HashMap<String, Object> assign = new HashMap<>();
					parameters.put("coaches", coaches);
					assign.put("userCredentials", userCredentials);
					assign.put("parameters", parameters);
					Map<String, Object> batchResponse = batchService.assignCoachToBatch(assign, userToken);
//					Map<String, Object> batchResponse = restTemplate.postForEntity(BatchAPI, data, Map.class);
//					 jsonResponse = batchResponse.getBody();
					if (batchResponse.get("status").toString().equals("Error")) {
						response.put("message", batchResponse.get("message").toString() + "! "
								+ "coach creation is successfull but assigning is failed");
						response.put("status", "Error");
						return response;
					}
					@SuppressWarnings("unchecked")
					List<Map<String, Object>> batchList = (List<Map<String, Object>>) data.get("batches");
					batches.addAll(batchList);
					response.put("batches", batches);

				}

				response.put("status", "Success");
				response.put("message", "Successfully Created");
				response.put("username", coachCredentials.getUsername());
				response.put("email", userPersonalDetails.getEmail());
				response.put("fullname", userPersonalDetails.getFullName());
				response.put("phoneNumber", userPersonalDetails.getPhoneNumber());
				return response;
			}
		} catch (Exception e) {
			logger.info("There is a error", e);
			response.put("status", "Error");
			response.put("message", "exception error");
			return response;
		}

	}

	// for Updating Data of Coach
	public Map<String, Object> updateCoach(HashMap<String, Object> data, String userToken) {
		Map<String, Object> response = new HashMap<>();
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> parameters = (HashMap<String, Object>) data.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> coach = (HashMap<String, Object>) parameters.get("parameters");
			@SuppressWarnings("unchecked")
			HashMap<String, Object> userCredentials = (HashMap<String, Object>) data.get("userCredentials");
			if (coach.get("email") == null || coach.get("email").toString().isEmpty()
					|| coach.get("coachId") == null || coach.get("coachId").toString().isEmpty()
					|| coach.get("fullName") == null || coach.get("fullName").toString().isEmpty()
					|| coach.get("phoneNumber") == null || coach.get("phoneNumber").toString().isEmpty()
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
			Long coachId = Long.parseLong(coach.get("coachId").toString().trim());
			String email = coach.get("email").toString().trim();
			String fullName = coach.get("fullName").toString().trim();
			String phoneNumber = coach.get("phoneNumber").toString().trim();
			String gender = coach.get("gender") == null ? "" : coach.get("gender").toString().trim();
			UserCredentials coachDetails = userCredentialRepository.findByUserId(coachId);

			if (coachDetails == null) {
				response.put("status", "Error");
				response.put("message", "Invalid !!");
				return response;
			} else {
				coachDetails.getUserPersonalDetails().setEmail(email);
				coachDetails.getUserPersonalDetails().setFullName(fullName);
				coachDetails.getUserPersonalDetails().setPhoneNumber(phoneNumber);
				coachDetails.getUserPersonalDetails().setGender(gender);
				userCredentialRepository.save(coachDetails);
				response.put("status", "Success");
				response.put("message", "Successfully Created");
				response.put("email", coachDetails.getUserPersonalDetails().getEmail());
				response.put("fullname", coachDetails.getUserPersonalDetails().getFullName());
				response.put("phoneNumber", coachDetails.getUserPersonalDetails().getPhoneNumber());
				if (coachDetails.getUserPersonalDetails().getGender() != null
						&& !coachDetails.getUserPersonalDetails().getGender().isEmpty()) {
					response.put("gender", coachDetails.getUserPersonalDetails().getGender());
				}
				response.put("status", "Success");
				response.put("message", "Successfully Updated");
				return response;
			}
		} catch (Exception e) {
			logger.info("There is a error", e);
			response.put("status", "Error");
			response.put("message", "Exception error occured");
			return response;
		}
	}

	// for sending email
	private void sendUserCredential(String email, String generatedPassword, String uniqueEmployeeId, String fullName) {
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTo(email);
		emailRequest.setSubject("Welcome to our Peninsula Community!");
		EmailRequest.Body body = new EmailRequest.Body();
		body.setGreeting("Dear, " + fullName + "!");
		body.setMain("We’re thrilled to welcome you to the Peninsula family!.Your EmployeeId is " + uniqueEmployeeId
				+ " Your Password is  " + generatedPassword);
		body.setFooter("Best Regards,Company");
		emailRequest.setBody(body);
		restTemplate.postForObject(EmailAPI, emailRequest, Void.class);

	}

	// Creating Unique Employment Id
	public static String generateEmployeeId() {
		String prefix = "EMP";
		// Get the last two digits of the current year
		String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		String lastTwoDigitsOfYear = year.substring(year.length() - 2);
		String suffix = "EID";
		// Generates a random 3-digit number
		int randomNumber = new Random().nextInt(900) + 100; // 100 to 999
		return prefix + lastTwoDigitsOfYear + suffix + randomNumber;
	}

}
