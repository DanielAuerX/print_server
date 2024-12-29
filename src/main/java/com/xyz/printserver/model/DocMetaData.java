package com.xyz.printserver.model;

import java.time.LocalDateTime;

public record DocMetaData(String fileName, String originalName, LocalDateTime timeOfRequest, String creatorIp,
                          String creatorUserAgent) {
}
