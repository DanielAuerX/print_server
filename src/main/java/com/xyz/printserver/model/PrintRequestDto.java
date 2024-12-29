package com.xyz.printserver.model;

import java.util.UUID;

public record PrintRequestDto(UUID docId, boolean print) {

}
