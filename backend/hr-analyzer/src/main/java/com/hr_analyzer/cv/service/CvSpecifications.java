package com.hr_analyzer.cv.service;

import com.hr_analyzer.cv.model.Cv;
import org.springframework.data.jpa.domain.Specification;

public class CvSpecifications {




    public static Specification<Cv> minScore(Double threshold) {
        return (root, q, cb) -> (threshold == null)
                ? cb.isNotNull(root.get("matchScore"))
                : cb.greaterThanOrEqualTo(root.get("matchScore"), threshold);
    }

    public static  Specification<Cv> byJobId(Long jobId)
    {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("job").get("id"), jobId);

    }










}
