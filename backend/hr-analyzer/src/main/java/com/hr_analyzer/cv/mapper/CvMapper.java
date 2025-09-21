package com.hr_analyzer.cv.mapper;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.model.CvSuggestion;
import com.hr_analyzer.job.model.Job;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


public class CvMapper {


    public static Cv mapToCv(User candidate, String cvContent ,Job job , Double aiData ) {
        return Cv.builder()
                .candidate(candidate)
                .cvContent(cvContent)
                .uploadTime(LocalDateTime.now())
                .job(job)
                .matchScore(aiData)
                .build();
    }

    public static CvResponse mapToResponse(Cv cv)
    {

        return CvResponse.builder()
                .id(cv.getId())
                .candidateLastName(cv.getCandidate().getLastName())
                .candidateFirstName(cv.getCandidate().getLastName())
                .email(cv.getCandidate().getEmail())
                .phoneNumber(cv.getCandidate().getPhone())
                .jobTitle(cv.getJob().getTitle())
                .matchScore(cv.getMatchScore())
                .uploadTime(cv.getUploadTime())
                .suggestion(cv.getSuggestions().stream().map(CvSuggestion::getSuggestionText).toList())
                .build();

    }










}
