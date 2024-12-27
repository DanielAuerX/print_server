package com.xyz.printserver.controller;

import com.xyz.printserver.service.UploadService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin //TODO adjust
public class UploadController {

    private final Bucket bucket;
    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(30)));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @PostMapping()
    public ResponseEntity<Object> uploadFile(@RequestBody MultipartFile file){
        if (bucket.tryConsume(1)) {
            uploadService.handleFile(file);
            return ResponseEntity.ok("Uploaded file");
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("too many requests :(");
    }
}
