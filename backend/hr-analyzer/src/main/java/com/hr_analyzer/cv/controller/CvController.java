package com.hr_analyzer.cv.controller;



import com.hr_analyzer.auth.dto.CandidateResponse;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.service.CvService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
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

        cvService.uploadCvWithFile(file, jobId);
        return ResponseEntity.ok("CV uspesno uploadovan");



    }



    @GetMapping(value = "/my-cvs")
    public ResponseEntity<List<CvResponse>> getLoggedUsersCvs() {
        try {
            List<CvResponse> cvResponses = cvService.getLoggedUsersCvs();

            if (cvResponses.isEmpty()) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(cvResponses);
            }

            return ResponseEntity.ok(cvResponses);


        } catch (Exception ex) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }



    @PreAuthorize("hasRole('HR')")
    @GetMapping("/top/{id}")
    public ResponseEntity<List<CandidateResponse>> getTopCandidateForJob(
            @PathVariable Long id
    )
    {


        List<CandidateResponse> candidateResponseList = cvService.getTopCandidatesForJob(id);
        return ResponseEntity.ok(candidateResponseList);


    }







}
