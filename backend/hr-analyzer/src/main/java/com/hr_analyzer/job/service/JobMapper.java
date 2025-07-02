package com.hr_analyzer.job.service;

import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class JobMapper {

    public static Job mapToJob(JobRequest jobRequest, User user)
    {
        return Job.builder()
                .title(jobRequest.getTitle())
                .description(jobRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .createdBy(user)
                .build();



    }


    public static JobResponse mapToResponse(Job job)
    {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .createdAt(job.getCreatedAt())
                .createdByUsername(job.getCreatedBy().getUsername())
                .build();


    }


}
