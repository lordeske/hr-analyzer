package com.hr_analyzer.cv.service;

import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.exception.AiAnalysisException;
import com.hr_analyzer.cv.mapper.CvMapper;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.model.CvSuggestion;
import com.hr_analyzer.cv.repository.CvRepository;

import com.hr_analyzer.cv.repository.CvSuggestionRepository;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CvService {


    @Autowired
    private CvRepository cvRepository;

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private CvScoringService cvScoringService;

    @Autowired
    private CohereScoringService cohereScoringService;

    @Autowired
    private CvSuggestionRepository cvSuggestionRepository;


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


    @Transactional
    public void uploadCvWithFile(MultipartFile file , Long jobId,
                                 String firstName, String lastName,
                                 String email, String phone) {



        User uploader = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Nema ulogovanog korisnika"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("Posao nije pronadjen"));


        String cvContent = extractTextFromFile(file);

        try {
            CvAnalysisResult aiData = cohereScoringService.analyzeCv(job.getDescription(), cvContent);

            if (aiData == null
                    || aiData.getMatchPercentage() == null
                    || aiData.getSuggestions() == null
                    || aiData.getSuggestions().isEmpty()) {

                throw new AiAnalysisException("Greska kod AI analize, pokusajte ponovo");

            }

            if(aiData.getMatchPercentage() > 100 || aiData.getMatchPercentage() < 0)
            {
                throw new AiAnalysisException("Greska kod AI analize, vratio je los match score");
            }

            CvUploadRequest cvUploadRequest = CvUploadRequest.builder()
                    .candidateFirstName(firstName)
                    .candidateLastName(lastName)
                    .phoneNumber(phone)
                    .email(email)
                    .jobId(jobId)
                    .cvContent(cvContent)
                    .build();

            Cv cv = CvMapper.mapToCv(cvUploadRequest, uploader, job, aiData.getMatchPercentage());
            cvRepository.save(cv);


            List<CvSuggestion> suggestionEntities = aiData.getSuggestions().stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .limit(10)
                    .map(s -> s.length() > 500 ? s.substring(0, 500) : s)
                    .map(text -> CvSuggestion.builder().suggestionText(text).cv(cv).build())
                    .toList();


            cvSuggestionRepository.saveAll(suggestionEntities);


        }
        catch (Exception ex)
        {
            log.error("AI analiza nije uspela za jobId={} (title={})", job.getId(), job.getTitle(), ex);
            throw ex;


        }

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
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MIN_TEXT_LENGTH = 500;

    private String extractTextFromFile(MultipartFile file) {
        try {

            if (file.isEmpty() || file.getOriginalFilename() == null) {
                throw new RuntimeException("Fajl nije dostavljen ili nema naziv.");
            }

            String filename = file.getOriginalFilename().toLowerCase();
            String mimeType = file.getContentType();


            boolean isPdf = (mimeType != null && mimeType.equals("application/pdf")) || filename.endsWith(".pdf");
            boolean isDocx = (mimeType != null && mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    || filename.endsWith(".docx");

            if (!isPdf && !isDocx) {
                throw new RuntimeException("Nepodržan tip fajla: " + mimeType);
            }


            if (file.getSize() > MAX_FILE_SIZE) {
                throw new RuntimeException("Fajl je prevelik (maksimalno " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB)");
            }


            if (isPdf) {
                try (PDDocument document = PDDocument.load(file.getInputStream())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    validateExtractedText(text);
                    return text;
                }
            }


            if (isDocx) {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
                     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                    String text = extractor.getText();
                    validateExtractedText(text);
                    return text;
                }
            }

            throw new RuntimeException("Nepodržan format fajla.");

        } catch (Exception e) {
            throw new RuntimeException("Greška pri čitanju fajla: " + e.getMessage());
        }
    }


    private void validateExtractedText(String text) {
        if (text == null || text.trim().length() < MIN_TEXT_LENGTH) {
            throw new RuntimeException("CV ne sadrži dovoljno teksta za analizu (minimum " + MIN_TEXT_LENGTH + " karaktera).");
        }
    }








}
