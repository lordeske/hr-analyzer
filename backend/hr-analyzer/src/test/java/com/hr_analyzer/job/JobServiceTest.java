package com.hr_analyzer.job;

import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.model.JobRequest;
import com.hr_analyzer.job.model.JobResponse;
import com.hr_analyzer.job.repository.JobRepository;
import com.hr_analyzer.job.service.JobService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
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






        JobRequest jobRequest = new JobRequest("Backend Developer", "Kompanija", "Lokacija", "Opis",
                BigDecimal.valueOf(300));


        when(jobRepository.save(any(Job.class   ))).thenReturn(TestData.job("Backend Developer"));

        var response = jobService.createJob(jobRequest);



        assertNotNull(response);
        assertEquals("Backend Developer", response.getTitle());
        verify(jobRepository, times(1)).save(any(Job.class));

        }
    }


    @Test
    void  getAllJobs_capsPageSizeAt100_andKeepsSort()
    {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable input = PageRequest.of(2, 200, sort);


        List<Job> jobList = List.of(
                TestData.job("Java Dev"),
                TestData.job("C Dev"));

        Page<Job> repoPage = new PageImpl<>(jobList,PageRequest.of(2,100, sort),250);



        when(jobRepository.findAll(any(Pageable.class))).thenReturn(repoPage);

        Page<JobResponse> result = jobService.getAllJobs(input);


        ArgumentCaptor<Pageable> pageableArgumentCaptor  = ArgumentCaptor.forClass(Pageable.class);
        verify(jobRepository).findAll(pageableArgumentCaptor.capture());

        Pageable used = pageableArgumentCaptor.getValue();

        assertEquals(100, used.getPageSize(), "page size mora biti cap-ovan na 100");
        assertEquals(2, used.getPageNumber(), "page number treba da ostane isti");
        assertEquals(sort, used.getSort(), "sort treba da ostane isti");

        assertEquals(2, result.getContent().size());
        assertEquals(250, result.getTotalElements());
        assertEquals("Java Dev", result.getContent().get(0).getTitle());

    }













    }






