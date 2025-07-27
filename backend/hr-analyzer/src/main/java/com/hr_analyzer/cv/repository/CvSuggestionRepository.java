package com.hr_analyzer.cv.repository;


import com.hr_analyzer.cv.model.CvSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CvSuggestionRepository extends JpaRepository<CvSuggestion, Long> {
}
