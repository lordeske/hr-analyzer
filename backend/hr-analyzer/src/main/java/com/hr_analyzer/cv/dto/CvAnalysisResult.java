package com.hr_analyzer.cv.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CvAnalysisResult {

    double matchPercentage;
    List<String> suggestions;
}
