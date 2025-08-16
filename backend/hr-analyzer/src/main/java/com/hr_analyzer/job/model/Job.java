package com.hr_analyzer.job.model;

import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.model.Cv;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "source_url", length = 2000)
    private String sourceUrl;


    @Column(name = "external_id", length = 100, unique = true )
    private String externalId;

    @Lob
    @Column(name = "description_snapshot", columnDefinition = "TEXT", nullable = false)
    private String descriptionSnapshot;

    private LocalDateTime createdAt;

    @ManyToOne
    private User createdBy;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cv> cvs;

    @PrePersist
    public void prePresist()
    {
        if(createdAt == null)
        {
            createdAt = LocalDateTime.now();
        }
    }


}
