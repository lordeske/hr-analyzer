package com.hr_analyzer.job;

import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.repository.JobRepository;
import com.hr_analyzer.job.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JobServiceTest {


    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;


    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetById_jobExists_returnsJobResponse()
    {

        Job job = TestData.job("Testni Title");


        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));

        var response = jobService.getById(job.getId());

        assertNotNull(response);
        assertEquals("Testni Title", response.getTitle());



    }


    @Test
    void testGetById_jobDoesNotExist_throwsException()
    {

        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class , ()-> jobService.getById(1L));


    }


    @Test
    void testCreateJob_success()
    {

        User user = TestData.user();


        try (MockedStatic<SecurityUtils> securityUtilsMockedStatic = mockStatic(SecurityUtils.class)) {
            securityUtilsMockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(Optional.of(user));


            System.out.println("Mokovani korisnik je: " + SecurityUtils.getCurrentUser().orElse(null));

        }




        JobRequest jobRequest = new JobRequest("Backend Developer", "Kompanija", "Lokacija", "Opis",
                BigDecimal.valueOf(300));


        when(jobRepository.save(any(Job.class   ))).thenReturn(TestData.job("Backend Developer"));

        var response = jobService.createJob(jobRequest);

        assertNotNull(response);
        assertEquals("Backend Developer", response.getTitle());
        verify(jobRepository, times(1)).save(any(Job.class));

    }













    }






