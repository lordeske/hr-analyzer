package com.hr_analyzer.cv.service;

import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.mapper.CvMapper;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.repository.CvRepository;

import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.repository.JobRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Security;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CvService {


    @Autowired
    private CvRepository cvRepository;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CvScoringService cvScoringService;

    public void uploadCv(CvUploadRequest cvUploadRequest)
    {

        User uploader = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Nema ulogovanog korisnika"));

        Job job = jobRepository.findById(cvUploadRequest.getJobId())
                .orElseThrow(() -> new RuntimeException("Posao nije pronađen"));


        double matchScore = cvScoringService.calculateMatchScore(job.getDescription(),
               cvUploadRequest.getCvContent());

        Cv cv = CvMapper.mapToCv(cvUploadRequest, uploader, job , matchScore);


        cvRepository.save(cv);


    }

    public List<CvResponse> getAllCvs()
    {
        return cvRepository.findAll()
                .stream()
                .map(CvMapper::mapToResponse)
                .collect(Collectors.toList());


    }

    public List<CvResponse> searchCvsByTitle(String job)
    {

        return  cvRepository.findByJobTitleContainingIgnoreCase(job)
                .stream()
                .map(CvMapper::mapToResponse)
                .collect(Collectors.toList());


    }

    public void uploadCvWithFile(MultipartFile file , Long jobId,
                                 String firstName, String lastName,
                                 String email, String phone) {



        User uploader = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("Nema ulogovanog korisnika"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Posao nije pronađen"));


        String cvContent = extractTextFromFile(file);

        double matchScore = cvScoringService.calculateMatchScore(job.getDescription(), cvContent);


        CvUploadRequest cvUploadRequest = CvUploadRequest.builder()
                .candidateFirstName(firstName)
                .candidateLastName(lastName)
                .phoneNumber(phone)
                .email(email)
                .jobId(jobId)
                .cvContent(cvContent)
                .build();

        Cv cv = CvMapper.mapToCv(cvUploadRequest, uploader, job, matchScore);

        cvRepository.save(cv);

    }




    public List<CvResponse> searchCvs(CvSearchRequest request)
    {

        List<Cv> cvs = cvRepository.findAll();

        Stream<Cv> stream  = cvs.stream();

        stream = stream.filter(cv -> cv.getMatchScore() != null);



        // filter po keyword
        if(request.getKeyword() != null && !request.getKeyword().isBlank())
        {

            String keyword = request.getKeyword().toLowerCase();
            stream = stream.filter(cv ->
                    cv.getCandidateFirstName().toLowerCase().contains(keyword) ||
                            cv.getCandidateLastName().toLowerCase().contains(keyword) ||
                            cv.getJob().getTitle().toLowerCase().contains(keyword)

            );



        }

        // filter po score

        if (request.getMinScore() != null)
            stream = stream.filter(cv -> cv.getMatchScore() >= request.getMinScore());

        if (request.getMaxScore() != null)
            stream = stream.filter(cv -> cv.getMatchScore() <= request.getMaxScore());



        /// Sort

        Comparator<Cv> comparator;

        if("matchScore".equalsIgnoreCase(request.getSortBy())) {
            comparator = Comparator.comparing(Cv::getMatchScore);
        }
        else {
            comparator = Comparator.comparing(Cv::getUploadTime);
        }

        if("desc".equalsIgnoreCase(request.getSortDirection()))
        {
            comparator = comparator.reversed();
        }

        return stream
                .sorted(comparator)
                .map(CvMapper::mapToResponse)
                .collect(Collectors.toList());


    }



    /// funkcije
    private String extractTextFromFile(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            if (filename.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                }
            } else if (filename.endsWith(".docx")) {
                XWPFDocument doc = new XWPFDocument(file.getInputStream());
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                return extractor.getText();
            } else {
                throw new RuntimeException("Nepodržan format fajla");
            }
        } catch (Exception e) {
            throw new RuntimeException("Greška pri čitanju fajla: " + e.getMessage());
        }
    }







}
