package com.hr_analyzer.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;

}
