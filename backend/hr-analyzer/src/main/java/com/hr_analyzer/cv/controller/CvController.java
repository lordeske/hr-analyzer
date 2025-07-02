package com.hr_analyzer.cv.controller;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.service.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cv")
public class CvController {


    @Autowired
    private CvService cvService;


    @PostMapping("/upload")
    public ResponseEntity<?> uploadCv(@RequestBody CvUploadRequest request) {
        cvService.uploadCv(request);
        return ResponseEntity.ok("CV uploadovan");
    }



    @GetMapping("/all")
    public ResponseEntity<List<CvResponse>> getAllCvs()
    {

        return ResponseEntity.ok(cvService.getAllCvs());

    }
    @GetMapping("/search")
    public ResponseEntity<List<CvResponse>> searchCvs(
            @RequestParam String  jobTitle
    )
    {

        return ResponseEntity.ok(cvService.searchCvsByTitle(jobTitle));

    }

    /*

    @PostMapping("/advancedSearch")
    public ResponseEntity<List<CvResponse>>  searchCvs(@RequestBody CvSearchRequest request) {
        return ResponseEntity.ok(cvService.searchCvs(request));
    }

     */






}
