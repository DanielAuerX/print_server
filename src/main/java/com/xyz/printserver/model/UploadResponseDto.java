package com.xyz.printserver.model;

import java.util.UUID;

public record UploadResponseDto (String fileName, UUID docId){}
