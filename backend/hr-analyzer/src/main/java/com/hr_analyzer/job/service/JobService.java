package com.hr_analyzer.job.service;


import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.dto.CandidateResponse;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.exception.CvNotFoundException;
import com.hr_analyzer.cv.mapper.CvMapper;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.repository.CvRepository;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import com.hr_analyzer.job.model.JobSearchRequest;
import com.hr_analyzer.job.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JobService {


    private final JobRepository jobRepository;


    private final CvRepository cvRepository;




    public JobResponse createJob(JobRequest request)
    {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new EntityNotFoundException("Nema ulogovanog korisnika"));


        Job job = JobMapper.mapToJob(request,user);
        Job savedJob = jobRepository.save(job);

        return JobMapper.mapToResponse(savedJob);


    }


    public Page<JobResponse> getAllJobs(Pageable pageable)
    {

        int size = Math.min(pageable.getPageSize(), 100);

        pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());

        Page<Job> page = jobRepository.findAll(pageable);

        return page.map(JobMapper::mapToResponse);

    }


    public Page<CvResponse> getAllCvsForJobId(Long jobId, Pageable pageable) {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Nisi ologovan, token ne valja"));

        Job job = jobRepository.findById(jobId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Ne postoji posao sa ID: " + jobId));

        if (!job.getCreatedBy().equals(user)) {
            throw new AccessDeniedException("Ne možeš da gledaš CV-ove za posao koji nisi ti kreirao");
        }

        Page<Cv> cvList = cvRepository.findByJobId(jobId, pageable);

        return cvList
                .map(CvMapper::mapToResponse);
    }


    public JobResponse getById(Long id) {

            Job job  = jobRepository.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException("Job nije pronadjen"));


            return JobMapper.mapToResponse(job);




    }

    public Page<JobResponse> getMyJobs(Pageable pageable) {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Nisi ologovan, token ne valja"));


        Page<Job> jobs  = jobRepository.findByCreatedBy(user, pageable);

        return jobs.map(JobMapper::mapToResponse);

    }


    public Page<JobResponse> advancedSearchJob(JobSearchRequest request, Pageable pageable)
    {

        String keyword = Optional.ofNullable(request.getKeyword())
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse(null);

        if(request.getMinSalary() != null && request.getMaxSalary() != null
            && request.getMinSalary().compareTo(request.getMaxSalary()) > 0
        )
            throw new IllegalStateException("Minimalna zarada ne moze biti veca od maksimalne");


        /// swap datuma radi sigurnosti
        LocalDateTime from = request.getFrom();
        LocalDateTime to   = request.getTo();
        if (from != null && to != null && from.isAfter(to)) {
            LocalDateTime tmp = from; from = to; to = tmp;
        }



        Sort sort = pageable.getSort().isUnsorted() ? Sort.by(Sort.Direction.DESC, "createdAt")
                : pageable.getSort();

        pageable = PageRequest.of(
                Math.max(0, pageable.getPageNumber()),
                Math.min(pageable.getPageSize(), 100),
                sort
        );

        Specification<Job> spec = Specification.where(JobSpecifications.keywordLike(keyword))
                .and(JobSpecifications.createdBetween(from, to))
                .and(JobSpecifications.minSalary(request.getMinSalary()))
                .and(JobSpecifications.maxSalary(request.getMaxSalary()));

        Page<Job> page = jobRepository.findAll(spec, pageable);

        return page.map(JobMapper::mapToResponse);


    }


}
