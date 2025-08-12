package com.hr_analyzer.cv.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CvAnalysisResult {

    Double matchPercentage;
    List<String> suggestions;
}
