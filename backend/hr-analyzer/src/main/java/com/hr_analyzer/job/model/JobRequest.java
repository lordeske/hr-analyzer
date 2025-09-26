package com.hr_analyzer.job.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class JobRequest {


    private String title;
    private String company;
    private String location;
    private String description;
    private BigDecimal salary;


}
