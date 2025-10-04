package com.hr_analyzer.cv.controller;



import com.hr_analyzer.auth.dto.CandidateResponse;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.kafka.CvKafkaProducer;
import com.hr_analyzer.cv.model.CvMyResponse;
import com.hr_analyzer.cv.service.CvService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/cv")
public class CvController {


    @Autowired
    private CvService cvService;


    @Autowired
    private CvKafkaProducer kafkaProducer;

//    @GetMapping("/all")
//    public ResponseEntity<List<CvResponse>> getAllCvs()
//    {
//
//        return ResponseEntity.ok(cvService.getAllCvs());
//
//    }


//    @GetMapping("/search")
//    public ResponseEntity<List<CvResponse>> searchCvs(
//            @RequestParam String  jobTitle
//    )
//    {
//
//        return ResponseEntity.ok(cvService.searchCvsByTitle(jobTitle));
//
//    }


//    --- OVO KASNIJE TREBA SREDITI NEKU LOGIKU SMISLITI
//    @PostMapping("/advancedSearch")
//    public ResponseEntity<List<CvResponse>>  searchCvs(@RequestBody CvSearchRequest request) {
//        return ResponseEntity.ok(cvService.searchCvs(request));
//    }


    @PostMapping(value = "/uploadCvFile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCvFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobId") @NotNull @Positive(message = "jobID mora da bude pozitivan") Long jobId


    )
    {

        kafkaProducer.sendCvUploadMessage(file, jobId);
        return ResponseEntity.ok("CV uspesno uploadovan");



    }



//    @GetMapping(value = "/my-cvs")
//    public ResponseEntity<List<CvResponse>> getLoggedUsersCvs() {
//
//            List<CvResponse> cvResponses = cvService.getLoggedUsersCvs();
//
//            return ResponseEntity.ok(cvResponses);
//
//    }



    @GetMapping(value = "/me/cvs")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<Page<CvMyResponse>> getLoggedUsersCvsSim(
            @PageableDefault(size = 20, sort = "uploadTime", direction = Sort.Direction.DESC)
            Pageable pageable
    )
    {

        Page<CvMyResponse> cvMyResponseList = cvService.getLoggedUsersCvsSim(pageable);

        return ResponseEntity.ok(cvMyResponseList);

    }



    @GetMapping(value = "/{id}")
    public ResponseEntity<CvResponse> getCv(
            @PathVariable Long id
    )
    {

        return ResponseEntity.ok(cvService.getCv(id));


    }




    @PreAuthorize("hasRole('HR')")
    @GetMapping("/top/{id}/{n}")
    public ResponseEntity<List<CandidateResponse>> getTopCandidateForJob(
            @PathVariable Long id,
            @RequestParam(name = "n", defaultValue = "5") Integer n,
            @RequestParam(name = "minScore", required = false) Double minScore
    )
    {


        List<CandidateResponse> candidateResponseList = cvService.getTopCandidatesForJob(id, n , minScore);
        return ResponseEntity.ok(candidateResponseList);


    }









}
