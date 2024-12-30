package com.xyz.printserver.controller;

import com.xyz.printserver.exception.DocIdNotFoundException;
import com.xyz.printserver.model.PrintRequestDto;
import com.xyz.printserver.model.UploadResponseDto;
import com.xyz.printserver.service.DocumentService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private final Bucket bucket;
    private final DocumentService documentService;
    private final int SECONDS_TO_WAIT = 30;
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);


    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(SECONDS_TO_WAIT)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) {
        if (bucket.tryConsume(1)) {
            final UploadResponseDto uploadResponseDto = documentService.save(file, request);
            return ResponseEntity.ok(uploadResponseDto);
        }
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("we are busy atm :( wait at least " + SECONDS_TO_WAIT + " seconds");
    }

    @PostMapping("/print")
    public ResponseEntity<Object> executePrintJob(@RequestBody PrintRequestDto printRequest, HttpServletRequest request){
        try{
            return ResponseEntity.ok(documentService.print(printRequest, request.getRemoteAddr()));
        } catch (DocIdNotFoundException | IllegalCallerException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body("bad request :(");
        }
    }

}
