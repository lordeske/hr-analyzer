package com.hr_analyzer.job.controller;


import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import com.hr_analyzer.job.model.JobSearchRequest;
import com.hr_analyzer.job.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class JobController {



    private final JobService jobService;




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
    public ResponseEntity<Page<JobResponse>> getAllJobs(
            @PageableDefault(size = 20, sort = "createdAt" , direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(jobService.getAllJobs(pageable));
    }

    @GetMapping("/{id}/cvs")
    public ResponseEntity<Page<CvResponse>> getAllCvsForJobId(
            @PathVariable Long id ,
            @PageableDefault(size = 20, sort = "matchScore" , direction = Sort.Direction.DESC) Pageable pageable
    )
    {

        return ResponseEntity.ok(jobService.getAllCvsForJobId(id ,pageable));

    }


    @GetMapping("/my-jobs")
    public ResponseEntity<Page<JobResponse>> getMyJobs(
            @PageableDefault(size = 20, sort = "createdAt" , direction = Sort.Direction.DESC)
            Pageable pageable
            )
    {

        Page<JobResponse> jobResponses = jobService.getMyJobs(pageable);

        return ResponseEntity.ok(jobResponses);

    }


    @PostMapping("/advancedSearch")
    public ResponseEntity<Page<JobResponse>> advancedSearchJob(
            @RequestBody JobSearchRequest jobSearchRequest,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    )
    {

        Page<JobResponse> jobResponses = jobService.advancedSearchJob(jobSearchRequest, pageable);

        return ResponseEntity.ok(jobResponses);
        

    }









}
