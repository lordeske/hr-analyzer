package com.hr_analyzer.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CvUploadRequest {

    private String candidateFirstName;
    private String candidateLastName;
    private String email;
    private String phoneNumber;
    private String cvContent;
    private Long jobId;
}
