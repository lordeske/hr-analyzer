package com.hr_analyzer.cv.mapper;


import com.hr_analyzer.auth.dto.CandidateResponse;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.model.CvMyResponse;
import com.hr_analyzer.cv.model.CvSuggestion;
import com.hr_analyzer.job.model.Job;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;


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
                .candidateFirstName(cv.getCandidate().getFirstName())
                .email(cv.getCandidate().getEmail())
                .phoneNumber(cv.getCandidate().getPhone())
                .jobTitle(cv.getJob().getTitle())
                .matchScore(cv.getMatchScore())
                .uploadTime(cv.getUploadTime())
                .jobId(cv.getJob().getId())
                .status(cv.getStatus().toString())
                .suggestion(cv.getSuggestions().stream().map(CvSuggestion::getSuggestionText).toList())
                .build();

    }


    public static CandidateResponse mapToCandidate(Cv cv)
    {

        return CandidateResponse.builder()
                .fullName(String.join(" ",
                        Optional.ofNullable(cv.getCandidate().getFirstName()).orElse(""),
                        Optional.ofNullable(cv.getCandidate().getLastName()).orElse("")
                ).trim())
                .email(cv.getCandidate().getEmail())
                .phone(cv.getCandidate().getEmail())
                .cvContent(cv.getCvContent())
                .matchScore(cv.getMatchScore())
                .suggestions(
                        cv.getSuggestions()
                                .stream()
                                .map(CvSuggestion::getSuggestionText)
                                .collect(Collectors.toList())
                )
                .build();


    }

    public static CvMyResponse mapTopCvMyResponse(Cv cv)
    {

        return CvMyResponse.builder()
                .cvId(cv.getId())
                .jobId(cv.getJob().getId())
                .companyName(cv.getJob().getCompany())
                .jobTitle(cv.getJob().getTitle())
                .matchScore(cv.getMatchScore())
                .build();



    }










}
