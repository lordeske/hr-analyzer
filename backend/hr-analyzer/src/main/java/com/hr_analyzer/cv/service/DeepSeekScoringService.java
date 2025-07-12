package com.hr_analyzer.cv.service;

import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DeepSeekScoringService {


    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double analyzeRelevance(String jobDescription, String cvContent) {
        String prompt = String.format("""
                You are an HR expert. Rate from 0 to 100 how much this CV matches the job description.

                Job description:
                %s

                CV:
                %s

                Give only the number.
                """, jobDescription, cvContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "inputs", prompt,
                "parameters", Map.of("max_new_tokens", 10)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.deepinfra.com/v1/inference/deepseek-ai/deepseek-llm-7b-chat",
                    entity,
                    Map.class
            );

            String text = ((Map<String, String>) ((List<?>) response.getBody().get("outputs")).get(0)).get("text");
            return Double.parseDouble(text.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
