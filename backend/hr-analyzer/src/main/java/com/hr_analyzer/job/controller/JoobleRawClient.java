package com.hr_analyzer.job.controller;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Controller
public class JoobleRawClient {



    @Value("${jooble.api.baseUrl}")
    private String baseUrl;
    @Value("${jooble.api.key}")
    private String apiKey;


    public final RestTemplate restTemplate = new RestTemplate();



    public String searchRaw(String keywords, String location, Integer page)
    {

        String url = baseUrl + "/" + apiKey;


        Map<String, Object> body = new HashMap<>();

        if (keywords != null) body.put("keywords", keywords);
        if (location != null) body.put("location", location);
        if (page != null) body.put("page", page);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(url, req, String.class);





    }








}
