package com.hr_analyzer.job.model;

import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.model.Cv;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.nio.DoubleBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "job")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;


    @Column
    private String company;

    @Column
    private String location;


    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cv> cvs;

    @Column( name = "salary")
    private BigDecimal salary;

    @PrePersist
    public void prePresist()
    {
        if(createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
    }


}
