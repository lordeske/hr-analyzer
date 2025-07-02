package com.hr_analyzer.cv.repository;

import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.model.Cv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CvRepository extends JpaRepository<Cv,Long> {


    List<Cv>  findByJobTitleContainingIgnoreCase(String JobTitle);


    List<Cv> findByJobId(Long jobId);
}
