package com.hr_analyzer.cv.service;

import com.hr_analyzer.auth.config.SecurityUtils;
import com.hr_analyzer.auth.dto.CandidateResponse;
import com.hr_analyzer.auth.model.User;
import com.hr_analyzer.auth.repository.UserRepository;
import com.hr_analyzer.cv.dto.CvAnalysisResult;
import com.hr_analyzer.cv.dto.CvResponse;
import com.hr_analyzer.cv.dto.CvSearchRequest;
import com.hr_analyzer.cv.dto.CvUploadRequest;
import com.hr_analyzer.cv.exception.AiAnalysisException;
import com.hr_analyzer.cv.exception.CvNotFoundException;
import com.hr_analyzer.cv.exception.EmptyCvContentException;
import com.hr_analyzer.cv.kafka.CvUploadMessage;
import com.hr_analyzer.cv.mapper.CvMapper;
import com.hr_analyzer.cv.model.Cv;
import com.hr_analyzer.cv.model.CvMyResponse;
import com.hr_analyzer.cv.model.CvSuggestion;
import com.hr_analyzer.cv.repository.CvRepository;

import com.hr_analyzer.cv.repository.CvSuggestionRepository;
import com.hr_analyzer.job.model.Job;
import com.hr_analyzer.job.repository.JobRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Security;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CvService {



    private final CvRepository cvRepository;

    private final JobRepository jobRepository;

    private final CvScoringService cvScoringService;

    private final CohereScoringService cohereScoringService;

    private final CvSuggestionRepository cvSuggestionRepository;

    private final UserRepository userRepository;


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




    @KafkaListener(topics = "cv-upload-topic" , groupId = "cv-group")
    @Transactional
//    @Retryable(
//           maxAttempts = 3,
//            backoff = @Backoff(delay = 5000)
//    )
    public void consumeCvUploadMessage(CvUploadMessage message) {

            log.info("Primio sam poruku iz Kafka: RADIM");
            log.info("Sending CV message for email: {}", message.getEmail());


        User candidate = userRepository.findByEmail(message.getEmail())
                .orElseThrow(() -> new IllegalStateException("Korisnik ne postoji"));


            Job job = jobRepository.findById(message.getJobId())
                    .orElseThrow(() -> new EntityNotFoundException("Posao nije pronadjen"));


            InputStream fileStream = new ByteArrayInputStream(message.getFile());
            String fileName = generateFileName(candidate.getId().toString());
            String mimType = message.getMimeType();
            long fileSize = message.getFile().length;


            String cvContent = extractTextFromFile(fileStream, fileName, mimType, fileSize);

            if (cvContent == null || cvContent.isEmpty()) {
                throw new EmptyCvContentException("CV koji ste predali je prazan, molimo upisite nesto u njega");
            }


            CvAnalysisResult aiData = cohereScoringService.analyzeCv(job.getDescription(), cvContent);

            if (aiData == null
                    || aiData.getMatchPercentage() == null
                    || aiData.getSuggestions() == null
                    || aiData.getSuggestions().isEmpty()) {

                throw new AiAnalysisException("Greska kod AI analize, pokusajte ponovo");

            }

            if (aiData.getMatchPercentage() > 100 || aiData.getMatchPercentage() < 0) {
                throw new AiAnalysisException("Greska kod AI analize, vratio je los match score");
            }


            Cv cv = CvMapper.mapToCv(candidate, cvContent, job, aiData.getMatchPercentage());
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
                    cv.getCandidate().getFirstName().toLowerCase().contains(keyword) ||
                            cv.getCandidate().getLastName().toLowerCase().contains(keyword) ||
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


//    public List<CvResponse> getLoggedUsersCvs() {
//
//
//
//        User user = SecurityUtils.getCurrentUser()
//                .orElseThrow(() -> new IllegalStateException("Nisi ologovan, token ne valja"));
//
//
//
//            List<Cv> cvs = cvRepository.findByCandidate(user);
//
//            if (cvs.isEmpty())
//            {
//                throw new CvNotFoundException("Nema CV-ova za ovog korisnika");
//            }
//
//
//
//
//
//        return cvs.stream()
//                .map(CvMapper::mapToResponse)
//                .collect(Collectors.toList());
//
//
//    }


    public List<CandidateResponse> getTopCandidatesForJob(Long id , Integer n , Double minScore) {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("Nisi ologovan, token ne valja"));


        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ne postoji posao sa ID: " + id));


        if(!user.getId().equals(job.getCreatedBy().getId()))
        {

            throw new AccessDeniedException("Ne možeš da gledaš CV-ove za posao koji nisi ti kreirao");

        }


        Double threshold = (minScore == null) ? null
                : Math.max(0.0, Math.min(minScore, 100.0));


        int size = (n == null || n < 1 ) ? 5 : Math.min(n, 50);

        Specification<Cv> spec = Specification.where(CvSpecifications.byJobId(id))
                .and(CvSpecifications.minScore(threshold));

        Sort sort = Sort.by(
                Sort.Order.desc("matchScore"),
                Sort.Order.desc("uploadTime"),
                Sort.Order.asc("id")
        );

        PageRequest pr = PageRequest.of(0, size, sort);

        Page<Cv> page = cvRepository.findAll(spec, pr);


        return page.getContent().stream()
                .map(CvMapper::mapToCandidate)
                .toList();
    }



    public CvResponse getCv(Long id) {

        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nisi ulogovan"));

        Cv cv = cvRepository.findById(id)
                .orElseThrow(() -> new CvNotFoundException("Ne postoji taj CV"));


        Long userId = user.getId();
        Long candidateId = cv.getCandidate().getId();
        Long creatorId   = cv.getJob().getCreatedBy().getId();


        if(!userId.equals(candidateId) && !userId.equals(creatorId))
        {
            throw new AccessDeniedException("Ne možeš da gledaš ovaj CV, nisi kandidat niti vlasnik posla");

        }

        return CvMapper.mapToResponse(cv);
    }



    public Page<CvMyResponse> getLoggedUsersCvsSim(Pageable pageable) {


        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nisi ulogovan"));



        int page = Math.max(0, pageable.getPageNumber());;
        int size = Math.min(Math.max(1, pageable.getPageSize()), 50);

        Set<String> allowedSorts = Set.of("uploadTime", "matchScore");

        Sort requested  = pageable.getSort();
        Sort safeSort = requested.isUnsorted()
                ? Sort.by(Sort.Direction.DESC, "uploadTime")
                : requested.stream()
                .filter(o -> allowedSorts.contains(o.getProperty()))
                .map(o -> o.with(o.getDirection()))
                .collect(Sort::unsorted, Sort::and, Sort::and);

        if(safeSort.isUnsorted())
        {
            safeSort = Sort.by(Sort.Direction.DESC, "uploadTime");
        }


        PageRequest pr = PageRequest.of(page, size, safeSort);

        Page<Cv> cvs = cvRepository.findByCandidateId(user.getId(),pr);


        return cvs.map(CvMapper::mapTopCvMyResponse);


    }








    /// funkcije
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MIN_TEXT_LENGTH = 500;

    private String extractTextFromFile(InputStream fileSteam, String filename, String mimeType, long fileSize) {
        try {



            if(fileSteam == null || filename == null)
            {
                throw new RuntimeException("Faijl nije dostavljen ili nema naziv");
            }


            String lowerFileName = filename.toLowerCase();


            boolean isPdf = (mimeType != null && mimeType.equals("application/pdf")) || filename.endsWith(".pdf");
            boolean isDocx = (mimeType != null && mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                    || filename.endsWith(".docx");

            if (!isPdf && !isDocx) {
                throw new RuntimeException("Nepodržan tip fajla: " + mimeType);
            }


            if (fileSize > MAX_FILE_SIZE) {
                throw new RuntimeException("Fajl je prevelik (maksimalno " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB)");
            }


            if (isPdf) {
                try (PDDocument document = PDDocument.load(fileSteam)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    String text = stripper.getText(document);
                    validateExtractedText(text);
                    return text;
                }
            }


            if (isDocx) {
                try (XWPFDocument doc = new XWPFDocument(fileSteam);
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


    public String generateFileName(String candidateId)
    {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateString = LocalDateTime.now().format(formatter);

        return dateString + "_" + candidateId;



    }



}
