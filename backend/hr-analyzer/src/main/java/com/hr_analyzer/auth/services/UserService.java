package com.hr_analyzer.auth.services;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()->
                        new UsernameNotFoundException("Korisnik sa emailom: " + email + " nije" +
                                "pronadjen"));


        return new CustomUserDetails(user);
    }
}
