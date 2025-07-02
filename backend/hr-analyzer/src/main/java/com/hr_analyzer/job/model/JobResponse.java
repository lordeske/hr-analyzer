package com.hr_analyzer.job.model;


import com.hr_analyzer.auth.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobResponse {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private String createdByUsername;


}
