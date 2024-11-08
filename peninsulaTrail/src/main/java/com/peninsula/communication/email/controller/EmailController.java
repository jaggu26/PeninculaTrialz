package com.peninsula.communication.email.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.peninsula.communication.email.model.EmailRequest;
import com.peninsula.communication.email.service.EmailService;

import jakarta.mail.MessagingException;

@RestController
public class EmailController {
	
	  @Autowired
	  private EmailService emailService;
	
	 @PostMapping("/send-email")
	  public void sendEmail(@RequestBody EmailRequest request) throws MessagingException {
	    emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody().getGreeting(),
	            request.getBody().getMain(), request.getBody().getFooter());
	  }
	
}
