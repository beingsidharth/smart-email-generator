package com.project.emailGenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.emailGenerator.model.EmailRequest;
import com.project.emailGenerator.service.EmailGeneratorService;

@CrossOrigin
@RestController
@RequestMapping("/api/email")
public class EmailController {
	
	@Autowired
	private EmailGeneratorService service;

	@PostMapping("/generate")
	public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
		
		String response = service.generateEmail(emailRequest);
		
		return ResponseEntity.ok(response);
	}
	
}
