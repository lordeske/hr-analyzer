package com.hr_analyzer.cv.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CvMyResponse {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long cvId;
    private Double matchScore;

}
