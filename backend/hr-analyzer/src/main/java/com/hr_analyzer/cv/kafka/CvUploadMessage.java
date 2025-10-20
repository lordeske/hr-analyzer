package com.hr_analyzer.cv.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CvUploadMessage {

    private byte[] file;
    private Long jobId;
    private String mimeType;
    private String  email;
    private Long cvId;



}
