package com.hr_analyzer.auth.services;


import com.hr_analyzer.auth.dto.AuthResponse;
import com.hr_analyzer.auth.dto.LoginRequest;
import com.hr_analyzer.auth.dto.RegisterRequest;
import com.hr_analyzer.auth.model.Role;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.auth.repository.UserRepository;
import com.hr_analyzer.auth.exceptions.UserAlreadyExistsException;
import com.hr_analyzer.auth.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;



    public boolean register(RegisterRequest request)
    {

        Optional<User> existingUser = userRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());

        if(existingUser.isPresent())
        {
            throw new UserAlreadyExistsException("Korisnik sa tim podacima vec postoji");
        }


        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.HR)
                .build();

        userRepository.save(user);

        return true;


    }

    public boolean registerCandidat(RegisterRequest request)
    {

        Optional<User> existingUser = userRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());


        if(existingUser.isPresent())
        {
            throw new UserAlreadyExistsException("Korisnik sa tim podacima vec postoji");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CANDIDATE)
                .build();

        userRepository.save(user);

        return true;


    }






    public AuthResponse login(LoginRequest request)
    {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails);
        String role = userDetails.getUser().getRole().toString();

        return new AuthResponse(jwt, role);




    }









}
