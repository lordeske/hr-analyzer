package com.hr_analyzer.cv.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CohereScoringService {

    @Value("${cohere.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    public CvAnalysisResult analyzeCv(String jobDescription, String cvContent) {
        String prompt = String.format("""
                You are an HR expert AI.
                    
                Evaluate how well the following CV matches the provided job description. Consider skills, experience, education, and relevant technologies.
                    
                Respond STRICTLY in this JSON format:
                    
                {
                  "matchPercentage": <number from 0 to 100>,
                  "suggestions": [
                    "<short suggestion 1>",
                    "<short suggestion 2>",
                    ...
                  ]
                }
                    
                Job Description:
                %s
                    
                CV:
                %s
                    
    """, jobDescription, cvContent);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "message", prompt,
                "model", "command-r-plus",
                "temperature", 0.2,
                "max_tokens", 60
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.cohere.ai/v1/chat",
                    entity,
                    Map.class
            );



            /// Vadim body
            Map<String, Object> responseBody = response.getBody();



            String textJson = responseBody.get("text").toString();

            Map<String, Object> message = objectMapper.readValue(textJson, new TypeReference<>() {});

            Integer matchScore = (Integer) message.get("matchPercentage");
            List<String> suggestions = objectMapper.convertValue(message.get("suggestions"), new TypeReference<>() {});

            return new CvAnalysisResult(matchScore,suggestions);








        } catch (Exception e) {
            log.error("Error during Cohere scoring", e);
            return new CvAnalysisResult(0.0, List.of("Error analyzing CV. Try again later."));
        }
    }


}