package com.hr_analyzer.job.controller;

import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.exception.ResponseStatusException;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import com.hr_analyzer.job.model.JobSearchRequest;
import com.hr_analyzer.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;



    @PostMapping("/create")
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }




    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/{id}/cvs")
    public ResponseEntity<List<CvResponse>> getAllCvsForJobId(
            @PathVariable Long id
    )
    {

        return ResponseEntity.ok(jobService.getAllCvsForJobId(id));

    }


    @GetMapping("/my-jobs")
    public ResponseEntity<List<JobResponse>> getMyJobs()
    {

        try {

            List<JobResponse> jobResponses = jobService.getMyJobs();

            if(jobResponses.isEmpty())
            {
                throw new ResponseStatusException("Jos nisi objavio poslove");
            }

            return ResponseEntity.ok(jobResponses);

        }
        catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }

    }


    @PostMapping("/advancedSearch")
    public ResponseEntity<List<JobResponse>> advancedSearchJob(
            @RequestBody JobSearchRequest jobSearchRequest
    )
    {


        

    }







}
