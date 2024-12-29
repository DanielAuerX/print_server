package com.xyz.printserver.service;

import com.xyz.printserver.exception.DocIdNotFoundException;
import com.xyz.printserver.exception.UploadFailedException;
import com.xyz.printserver.model.DocIdContainer;
import com.xyz.printserver.model.DocMetaData;
import com.xyz.printserver.model.PrintRequestDto;
import com.xyz.printserver.model.UploadResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Value("${printserver.files.directory}")
    private String fileDirectory;

    @Value("${printserver.files.prefix}")
    private String filePrefix;

    private final DocIdContainer docIdContainer;

    public DocumentService(DocIdContainer docIdContainer) {
        this.docIdContainer = docIdContainer;
    }

    public UploadResponseDto save(MultipartFile file, HttpServletRequest metaData) {
        try {
            prepareUploadDirectory();
            LocalDateTime timeOfRequest = LocalDateTime.now();
            final String fileName = filePrefix + timeOfRequest;
            final String filePath = fileDirectory + fileName;
            file.transferTo(new File(filePath));
            final DocMetaData docMetaData = new DocMetaData(fileName, file.getOriginalFilename(),
                    timeOfRequest, metaData.getRemoteAddr(), metaData.getHeader("User-Agent"));
            return generateResponse(docMetaData);
        } catch (IOException e) {
            log.error("failed to save the uploaded file", e);
            throw new UploadFailedException(e.getMessage());
        }
    }

    private UploadResponseDto generateResponse(final DocMetaData docMetaData) {
        final UUID docId = UUID.randomUUID();
        docIdContainer.add(docId, docMetaData);
        log.info("saved doc {}: {}", docId, docMetaData);
        return new UploadResponseDto(docMetaData.originalName(), docId);
    }

    private void prepareUploadDirectory() {
        File uploadDir = new File(fileDirectory);
        if (!uploadDir.exists()) {
            if (uploadDir.mkdirs()) {
                log.info("{} has been created", fileDirectory);
            }
        }
    }

    public String print(PrintRequestDto printRequest, String remoteAddr) {
        final DocMetaData docMetaData = docIdContainer.getValue(printRequest.docId());
        guardAgainstNull(docMetaData);
        guardAgainstIllegalCaller(remoteAddr, docMetaData);
        if (!printRequest.print()) {
            File file = new File(fileDirectory + docMetaData.fileName());
            if (file.delete()) {
                log.info("deleted file '{}'. initiated by {}", docMetaData.fileName(), docMetaData.creatorIp());
            } else {
                log.warn("failed to delete file '{}'", docMetaData.fileName());
            }
            return "nothing has been printed :)";
        }
        log.info("printing doc {}", printRequest.docId());
        // print file ...
        return "file has been printed :)";
    }

    private void guardAgainstIllegalCaller(final String remoteAddr, final DocMetaData docMetaData) {
        if (!docMetaData.creatorIp().equals(remoteAddr)) {
            throw new IllegalCallerException("print request has not been called by the same ip as the upload");
        }
    }

    private void guardAgainstNull(final DocMetaData docMetaData) {
        if (docMetaData == null) {
            throw new DocIdNotFoundException("container does not container the requested doc id");
        }
    }
}
