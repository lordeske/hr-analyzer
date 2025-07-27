package com.hr_analyzer.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CvResponse {
    private Long id;
    private String candidateFirstName;
    private String candidateLastName;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private Double matchScore;
    private LocalDateTime uploadTime;
    private String uploadedByUsername;
    private List<String> suggestion;
}
