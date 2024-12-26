package com.xyz.printserver.service;

import com.xyz.printserver.exception.UploadFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UploadService {

    private static final Logger log = LoggerFactory.getLogger(UploadService.class);
    private final String UPLOAD_DIR = "/tmp/upload/"; //TODO: env var

    public void handleFile(MultipartFile file) {
        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                if(uploadDir.mkdirs()){
                    log.info("{} has been created", UPLOAD_DIR);
                }
            }

            final String fileName = "print_request_" + LocalDateTime.now();
            final String filePath = UPLOAD_DIR + fileName;
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("failed to save the uploaded file.", e);
            throw new UploadFailedException(e.getMessage());
        }
    }
}
