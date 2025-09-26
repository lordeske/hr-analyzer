package com.hr_analyzer.cv.repository;

import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.model.Cv;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CvRepository extends JpaRepository<Cv,Long> {


    List<Cv>  findByJobTitleContainingIgnoreCase(String JobTitle);


    Page<Cv> findByJobId(Long jobId , Pageable pageable);

    List<Cv> findByCandidate(User user);



    List<Cv> findTop3ByJobIdOrderByMatchScoreDesc(Long jobId);

}
