package com.hr_analyzer.cv.model;


import com.hr_analyzer.auth.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private String candidateFirstName;
    private String candidateLastName;
    private String email;
    private String phoneNumber;
    private String jobTitle;

    @Column(length = 10000)
    private String cvContent;

    private Double matchScore;

    private LocalDateTime uploadTime;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy; // HR
}
