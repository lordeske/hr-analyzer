package com.hr_analyzer.cv.kafka;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CvKafkaProducer {

    private static final String TOPIC = "cv-upload-topic";



    @Autowired
    private KafkaTemplate<String, CvUploadMessage> kafkaTemplate;



    public void sendCvUploadMessage(CvUploadMessage cvUploadMessage)
    {


        kafkaTemplate.send(TOPIC, cvUploadMessage);

    }



}
