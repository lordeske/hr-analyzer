package com.hr_analyzer.job.model;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Builder
public class JobResponse {
    private Long id;
    private String title;
    private String company;
    private String location;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
}
