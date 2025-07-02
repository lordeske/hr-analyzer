package com.hr_analyzer.cv.service;


import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CvScoringService {

    public double calculateMatchScore(String jobDescription, String cvContent) {
        if (jobDescription == null || cvContent == null) return 0;

        jobDescription = jobDescription.toLowerCase();
        cvContent = cvContent.toLowerCase();

        String[] keywords = jobDescription.split("\\W+");
        long matches = Arrays.stream(keywords)
                .filter(word -> word.length() > 3)
                .filter(cvContent::contains)
                .count();

        return Math.min(100.0, (matches * 100.0) / keywords.length);
    }







}
