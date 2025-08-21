package com.hr_analyzer.cv.mapper;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.model.CvSuggestion;
import com.hr_analyzer.job.model.Job;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


public class CvMapper {


    public static Cv mapToCv(CvUploadRequest request, User uploadedBy, Job job , Double aiData ) {
        return Cv.builder()
                .candidateFirstName(request.getCandidateFirstName())
                .candidateLastName(request.getCandidateLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .cvContent(request.getCvContent())
                .uploadTime(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .job(job)
                .matchScore(aiData)
                .build();
    }

    public static CvResponse mapToResponse(Cv cv)
    {

        return CvResponse.builder()
                .id(cv.getId())
                .candidateLastName(cv.getCandidateLastName())
                .candidateFirstName(cv.getCandidateFirstName())
                .email(cv.getEmail())
                .phoneNumber(cv.getPhoneNumber())
                .jobTitle(cv.getJob().getTitle())
                .matchScore(cv.getMatchScore())
                .uploadTime(cv.getUploadTime())
                .uploadedByUsername(cv.getUploadedBy().getEmail())
                .suggestion(cv.getSuggestions().stream().map(CvSuggestion::getSuggestionText).toList())
                .build();

    }










}
