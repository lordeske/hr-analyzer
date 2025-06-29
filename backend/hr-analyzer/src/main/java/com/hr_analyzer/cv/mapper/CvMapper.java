package com.hr_analyzer.cv.mapper;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.model.Cv;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


public class CvMapper {


    public static Cv mapToCv(CvUploadRequest request, User uploadedBy, double matchScore) {
        return Cv.builder()
                .candidateFirstName(request.getCandidateFirstName())
                .candidateLastName(request.getCandidateLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .jobTitle(request.getJobTitle())
                .cvContent(request.getCvContent())
                .matchScore(matchScore)
                .uploadTime(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .build();
    }

    public static CvResponse mapToResponse(Cv cv)
    {

        return CvResponse.builder()
                .id(cv.getId())
                .candidateFirstName(cv.getCandidateFirstName())
                .candidateLastName(cv.getCandidateLastName())
                .email(cv.getEmail())
                .phoneNumber(cv.getPhoneNumber())
                .jobTitle(cv.getJobTitle())
                .matchScore(cv.getMatchScore())
                .uploadTime(cv.getUploadTime())
                .uploadedByUsername(cv.getUploadedBy().getUsername())
                .build();



    }










}
