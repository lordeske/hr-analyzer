package com.hr_analyzer.cv.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.exception.AiAnalysisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.DoubleBuffer;
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


        String safeCv = cvContent.length() > 12000 ? cvContent.substring(0, 12000) : cvContent;

        String prompt = String.format("""
            You are an HR expert AI.
            Evaluate how well the following CV matches the provided job description. Consider skills, experience, education, and relevant technologies.
            Please make sure the suggestions are in the language in which the CV was submitted.
            Respond STRICTLY in this JSON format:
            {
              "matchPercentage": <number from 0 to 100>,
              "suggestions": [
                "<short suggestion 1>",
                "<short suggestion 2>"
              ]
            }

            Job Description:
            %s

            CV:
            %s
            """, jobDescription, safeCv);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);


        Map<String, Object> body = Map.of(
                "model", "command-r-plus-08-2024",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.2,
                "max_tokens", 200,
                "response_format", Map.of("type", "json_object")
        );


        HttpEntity<Map<String , Object>> entity = new HttpEntity<>(body,headers);


        try {

            ResponseEntity<Map> response = restTemplate.postForEntity("https://api.cohere.com/v2/chat",
                    entity, Map.class);


            Map<String, Object> resposneBody = response.getBody();
            if(resposneBody == null)
            {
                throw new AiAnalysisException("AI odgovor je prazan");
            }


            Map<String ,  Object> message = (Map<String, Object>) resposneBody.get("message");
            if(message == null)
            {
                throw new AiAnalysisException("Nedostaje message u Odgovori od AI");
            }

            List<Map<String , Object>> content = (List<Map<String, Object>>) message.get("content");
            if(content == null || content.isEmpty())
            {
                throw new AiAnalysisException("Nedostaje message.content u odgovoru od AI");
            }


            /// Rad sa type text
            String textJson = null;
            for (Map<String, Object> block : content)
            {
                Object type = block.get("type");
                if("text".equals(type))
                {
                    textJson = String.valueOf(block.get("text"));
                    break;
                }
            }

            if(textJson == null || textJson.isBlank())
            {
                throw new AiAnalysisException("AI nije vratio text sadrzaj");
            }

            Map<String, Object> payload = objectMapper.readValue(textJson, new TypeReference<>() {});
            Object rawScore = payload.get("matchPercentage");
            if (rawScore == null) throw new AiAnalysisException("Nedostaje 'matchPercentage' u AI JSON-u");

            Double match = (rawScore instanceof Number n) ? n.doubleValue() : Double.parseDouble(rawScore.toString());
            List<String> suggestions = objectMapper.convertValue(
                    payload.getOrDefault("suggestions", List.of()), new TypeReference<List<String>>() {}
            );

            return new CvAnalysisResult(match, suggestions);








        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        catch (HttpStatusCodeException e) {
            int code = e.getStatusCode().value();
            String bodyStr = e.getResponseBodyAsString();
            log.error("Cohere HTTP error: status={} body={}", code, bodyStr);

            if (code == 401) throw new AiAnalysisException("Nevažeći API ključ za AI (401 Unauthorized)", e);
            if (code == 403) throw new AiAnalysisException("Zabranjen pristup AI servisu (403)", e);
            if (code == 429) throw new AiAnalysisException("AI rate limit – pokušajte kasnije (429)", e);
            if (code >= 500) throw new AiAnalysisException("AI servis trenutno nedostupan (" + code + ")", e);
            throw new AiAnalysisException("Neuspešna AI analiza (" + code + ")", e);

        } catch (AiAnalysisException e) {
            throw e;
        } catch (Exception e) {
            log.error("Greska tokom AI poziva u Cohere", e);
            throw new AiAnalysisException("Greška kod AI analize, pokušajte ponovo", e);
        }


    }



}