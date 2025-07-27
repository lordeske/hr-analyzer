package com.hr_analyzer.cv.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "cv_suggestion")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CvSuggestion {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "text")
    private String suggestionText;


    @ManyToOne
    @JoinColumn(name = "cv_id")
    private  Cv cv;

}
