package com.hr_analyzer.auth.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CandidateResponse {

    private String fullName;

    private String email;

    private String phone;

    private String cvContent;

    private Double matchScore;

    private List<String> suggestions;



}
