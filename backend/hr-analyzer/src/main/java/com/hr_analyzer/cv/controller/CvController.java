package com.hr_analyzer.cv.controller;



import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.service.CvService;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/cv")
public class CvController {


    @Autowired
    private CvService cvService;


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



    @PostMapping("/advancedSearch")
    public ResponseEntity<List<CvResponse>>  searchCvs(@RequestBody CvSearchRequest request) {
        return ResponseEntity.ok(cvService.searchCvs(request));
    }


    @PostMapping(value = "/uploadCvFile" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadCvFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobId") @NotNull @Positive(message = "jobID mora da bude pozitivan") Long jobId,
            @RequestParam("firstName") @NotBlank(message = "Ime je obavezno") String firstName,
            @RequestParam("lastName") @NotBlank(message = "Prezime je obavezno") String lastname,
            @RequestParam("email") @Email(message = "Email nije validan") String email,
            @RequestParam("phone") @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Telefon nije validan") String phone
    )
    {

        cvService.uploadCvWithFile(file, jobId, firstName, lastname, email, phone);
        return ResponseEntity.ok("CV uspesno uploadovan");



    }







}
