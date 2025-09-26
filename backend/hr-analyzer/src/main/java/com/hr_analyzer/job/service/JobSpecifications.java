package com.hr_analyzer.job.service;

import com.hr_analyzer.job.model.Job;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class JobSpecifications {


    public static Specification<Job> keywordLike(String keyword)
    {

        return ((root, query, criteriaBuilder) -> {

            if(keyword == null || keyword.isBlank()) return null;

            String kw = "%" + keyword.toLowerCase() + "%";


            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), kw),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), kw),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), kw)
            );

        });
    }


    public static Specification<Job> minSalary(BigDecimal min)
    {
        return (root, query, criteriaBuilder) ->
                (min == null) ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("salary"),min);

    }


    public static Specification<Job> maxSalary(BigDecimal max)
    {
        return ((root, query, criteriaBuilder) ->
                (max == null) ? null : criteriaBuilder.lessThanOrEqualTo(root.get("salay"), max));

    }

    public static Specification<Job> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, criteriaBuilder) -> {
            if (from == null && to == null) return null;
            if (from != null && to != null)  return criteriaBuilder.between(root.get("createdAt"), from, to);
            if (from != null)                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
            return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
        };
    }



    }




