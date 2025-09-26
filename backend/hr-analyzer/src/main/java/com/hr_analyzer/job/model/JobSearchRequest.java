package com.hr_analyzer.job.model;


import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSearchRequest {

    private String keyword;

    @DecimalMin(value = "0.0", inclusive = true, message = "Min salary mora biti ≥ 0")
    private BigDecimal minSalary;

    @DecimalMin(value = "0.0", inclusive = true, message = "Max salary mora biti ≥ 0")
    private BigDecimal maxSalary;

    private LocalDateTime from;
    private LocalDateTime to;




}
