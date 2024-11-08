package com.peninsula.registration.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.peninsula.common.utils.Utils;
import com.peninsula.communication.email.model.EmailRequest;
import com.peninsula.registration.model.UserCredentials;
import com.peninsula.registration.model.UserPersonalDetails;
import com.peninsula.registration.repository.RegistrationRepositoryForPersonalDetails;
import com.peninsula.registration.repository.RegistrationRepositoryForUserCredential;

@Service
public class RegistrationService {
		
	private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
	
	@Autowired
	private RegistrationRepositoryForPersonalDetails registrationRepositoryForPersonalDetails;
	
	@Autowired
	private RegistrationRepositoryForUserCredential registrationRepositoryForUserCredential;
	

	
	RestTemplate restTemplate = new RestTemplate();
	private static final String EmailAPI = "http://localhost:8080/send-email";
	
	public Map<String, Object> saveRegistrationDetails(HashMap<String, Object> data) {
		Map<String, Object> response=new HashMap<>();
		
		try {
			response.clear();
			String email=data.get("email").toString().trim();
			boolean isEmailExist=registrationRepositoryForPersonalDetails.findEmailExistOrNot(email);
			//checking email exist or not
			if(isEmailExist) {
				response.put("status", "Error");
				response.put("message", "Email is already exist");
				return response;
			}
			else {
				
				//saving to personal details table
				UserPersonalDetails userPersonalDetails=new UserPersonalDetails();
				userPersonalDetails.setEmail(data.get("email").toString().trim());
				userPersonalDetails.setFullName(data.get("fullName").toString().trim());
				userPersonalDetails.setPhoneNumber(data.get("phoneNumber").toString().trim());
				registrationRepositoryForPersonalDetails.save(userPersonalDetails);
				
				//saving to user credential table
				UserCredentials userCredentials=new UserCredentials();
				userCredentials.setUsername(data.get("email").toString().trim());
				String generatedPassword=Utils.generatePassword();
				sendUserCredential(data.get("email").toString().trim(),generatedPassword,userPersonalDetails.getFullName());//calling sending email
				userCredentials.setPassword(Utils.hashPassword(generatedPassword));
				userPersonalDetails.setUserCredentials(userCredentials);
				userCredentials.setUserPersonalDetails(userPersonalDetails);
				registrationRepositoryForUserCredential.save(userCredentials);
				
				response.put("status", "Success");
				response.put("message", "Successfully registered");
				response.put("userId", userCredentials.getUserId());
				return response;
				
				
				
			}
			
			
		}
		catch(Exception e){
				logger.info("There is a error",e);
				response.put("status", "Error");
			return response;
		}
		
		
	}
	
	
	//for sending email
	private void sendUserCredential(String email, String generatedPassword, String fullName) {
		EmailRequest emailRequest=new EmailRequest();
		emailRequest.setTo(email);
		emailRequest.setSubject("Welcome to our Peninsula Community!");
		EmailRequest.Body body=new EmailRequest.Body();
		body.setGreeting("Dear, "+fullName+ "!");
		body.setMain("Thank you for registering with us. We are glad to have you on board. Your Password is  "+generatedPassword);
		body.setFooter("Best Regards,Company");
		emailRequest.setBody(body);
		restTemplate.postForObject(EmailAPI, emailRequest, Void.class);
		    
		
	}





	
	
	
}
