package com.hr_analyzer.job.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jooble")
@RequiredArgsConstructor
public class JoobleDebugController {

    private final JoobleRawClient client;

    @PostMapping("/raw")
    public ResponseEntity<String> raw(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer page
    ) {
        String json = client.searchRaw(keywords, location, page);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }


}
