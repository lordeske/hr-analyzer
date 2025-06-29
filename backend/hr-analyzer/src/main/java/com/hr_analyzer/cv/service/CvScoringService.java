package com.hr_analyzer.cv.service;


import org.springframework.stereotype.Service;

@Service
public class CvScoringService {

    public double calculateMatchScore(String jobTitle, String cvContent)
    {

        if(jobTitle == null || cvContent == null)
            return 0;


        jobTitle = jobTitle.toLowerCase();
        cvContent = cvContent.toLowerCase();


        if(cvContent.contains(jobTitle))

            return 90;

        else
            return 40;


    }






}
