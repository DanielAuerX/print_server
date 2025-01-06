package com.xyz.printserver.model;

import java.util.UUID;

public record PrintRequestDto(UUID docId, boolean print, int numberOfCopies, int pagesPerSide, PrintRange printRange, Integer rangeStart, Integer rangeEnd) {

}
