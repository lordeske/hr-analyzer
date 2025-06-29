package com.hr_analyzer.cv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CvUploadRequest {

    private String candidateFirstName;
    private String candidateLastName;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String cvContent;
}
