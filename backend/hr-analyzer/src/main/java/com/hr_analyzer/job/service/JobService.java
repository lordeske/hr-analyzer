package com.hr_analyzer.job.service;


import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.mapper.CvMapper;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.repository.CvRepository;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import com.hr_analyzer.job.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CvRepository cvRepository;




    public JobResponse createJob(JobRequest request)
    {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new EntityNotFoundException("Nema ulogovanog korisnika"));


        Job job = JobMapper.mapToJob(request,user);
        Job savedJob = jobRepository.save(job);

        return JobMapper.mapToResponse(savedJob);


    }


    public List<JobResponse> getAllJobs()
    {

        return jobRepository.findAll()
                .stream()
                .map(JobMapper::mapToResponse)
                .collect(Collectors.toList());

    }


    public List<CvResponse> getAllCvsForJobId(Long jobId) {

         List<Cv> cvList =  cvRepository.findByJobId(jobId);

         if(cvList.isEmpty())
         {
             throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Niko se jos nije prijavio za ovu poziciju");
         }

         return  cvList.stream()
                 .map(CvMapper::mapToResponse )
                 .collect(Collectors.toList());


    }

    public JobResponse getById(Long id) {

            Job job  = jobRepository.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException("Job nije pronadjen"));


            return JobMapper.mapToResponse(job);




    }
}
