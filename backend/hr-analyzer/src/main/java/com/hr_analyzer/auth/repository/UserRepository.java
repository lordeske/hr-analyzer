package com.hr_analyzer.auth.repository;

import com.hr_analyzer.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {



    Optional<User> findByEmail(String email);
    Optional<User> findByEmailOrPhone(String email, String phone);

}
