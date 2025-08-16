package com.hr_analyzer.job.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {


    private String title;
    private String company;
    private String location;
    private String sourceUrl;
    private String externalId;
    private String descriptionSnapshot;

}
