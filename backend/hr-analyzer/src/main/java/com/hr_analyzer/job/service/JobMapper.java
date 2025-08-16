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
                .company(jobRequest.getCompany())
                .sourceUrl(jobRequest.getSourceUrl())
                .externalId(jobRequest.getExternalId())
                .descriptionSnapshot(jobRequest.getDescriptionSnapshot())
                .createdBy(user)
                .build();


    }


    public static JobResponse mapToResponse(Job job)
    {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .company(job.getCompany())
                .location(job.getLocation())
                .sourceUrl(job.getSourceUrl())
                .externalId(job.getExternalId())
                .descriptionSnapshot(job.getDescriptionSnapshot())
                .createdBy(job.getCreatedBy().getUsername())
                .createdAt(job.getCreatedAt())
                .build();
    }





}
