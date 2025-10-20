package com.hr_analyzer.cv.model;


import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cv")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cv {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private User candidate;

    @Column(length = 10000)
    private String cvContent;

    private LocalDateTime uploadTime;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private Double matchScore;

    @OneToMany(mappedBy = "cv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CvSuggestion> suggestions;

    @Enumerated(EnumType.STRING)
    private Status status;


}
