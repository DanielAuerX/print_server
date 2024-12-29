package com.xyz.printserver.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HouseKeepingServiceTest {

    @Test
    @Disabled("only run if files should be deleted")
    void cleanUpFiles() {
        HouseKeepingService houseKeepingService = new HouseKeepingService("/tmp/upload/", "print_request_");
        houseKeepingService.cleanUpFiles();
    }
}