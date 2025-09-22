package com.hr_analyzer.job.repository;

import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByCreatedBy(User user);


}
