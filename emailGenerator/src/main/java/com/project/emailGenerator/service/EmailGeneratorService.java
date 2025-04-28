package com.project.emailGenerator.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.emailGenerator.model.EmailRequest;

@Service
public class EmailGeneratorService {
	

	private final WebClient webClient;
	
	public EmailGeneratorService(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder.build();
	}
	
	
	@Value("${gemini.api.url}")
	private String geminiApiUrl;
	
	@Value("${gemini.api.key}")
	private String geminiApiKey;
	
	public String generateEmail(EmailRequest emailRequest) {
		
		//build a prompt
		
		String prompt = buildPrompt(emailRequest);
		
		// craft a request	
		Map<String, Object> request = Map.of(
				
				"contents", new Object[] {
						Map.of("parts", new Object[] {
								Map.of("text", prompt)
						})
				}
			);
		
		//make request and get response
		String response = webClient.post()
						  .uri(geminiApiUrl + geminiApiKey)
						  .header("Content-Type", "application/json")
						  .bodyValue(request)
						  .retrieve()
						  .bodyToMono(String.class)
						  .block();
		
		// extract and return response
		return extractResponse(response);
	}
	
	public String buildPrompt(EmailRequest emailRequest) {
		
		StringBuilder prompt = new StringBuilder();
		
		prompt.append("Generate a professional email reply for the following email content. Please don't generate a subject line ");
		
		if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
			prompt.append("Use a ").append(emailRequest.getTone()).append(" tone."); }
		
		prompt.append("\nOriginal mail: \n").append(emailRequest.getEmailContent());
		
		System.out.println(prompt);
		
		return prompt.toString();
		
	}
	
	public String extractResponse(String response) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(response);
			return rootNode.path("candidates")
						.get(0)
						.path("content")
						.path("parts")
						.get(0)
						.path("text")
						.asText();
			
		}catch(Exception e) {
			return "unable to process "+ e.getMessage();
		}
	}
}
