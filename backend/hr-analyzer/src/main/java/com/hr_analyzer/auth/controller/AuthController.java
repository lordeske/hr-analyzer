package com.hr_analyzer.auth.controller;


import com.hr_analyzer.auth.dto.AuthResponse;
import com.hr_analyzer.auth.dto.LoginRequest;
import com.hr_analyzer.auth.dto.RegisterRequest;
import com.hr_analyzer.auth.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<Boolean> register(
            @RequestBody RegisterRequest request)
    {

        boolean success = authService.register(request);
        return ResponseEntity.ok(success);


    }


    @PostMapping("/registerCandidat")
    public ResponseEntity<Boolean> registerCandidat(
            @RequestBody RegisterRequest request)
    {

        boolean success = authService.registerCandidat(request);
        return ResponseEntity.ok(success);


    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request)
    {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);


    }



}
