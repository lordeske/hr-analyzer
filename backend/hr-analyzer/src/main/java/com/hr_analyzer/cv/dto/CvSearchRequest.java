package com.hr_analyzer.cv.dto;

import lombok.Data;

@Data
public class CvSearchRequest {

    public String keyword;
    public Double minScore;
    public Double maxScore;
    private String sortBy; // "uploadTime" ili "matchScore"
    private String sortDirection;


}


//{
//        "keyword": "frontend",
//        "minScore": 60,
//        "maxScore": 100,
//        "sortBy": "matchScore",
//        "sortDirection": "desc"
//        }

