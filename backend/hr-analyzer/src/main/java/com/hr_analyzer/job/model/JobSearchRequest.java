package com.hr_analyzer.job.model;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobSearchRequest {

    private String keyword;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String sortBy; // "createdAt" ili "salary"
    private String sortDirection;



}
