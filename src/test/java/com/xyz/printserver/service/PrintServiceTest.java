package com.xyz.printserver.service;

import com.xyz.printserver.model.PrintRange;
import com.xyz.printserver.model.PrintRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class PrintServiceTest {
    @Test
    //@Disabled("Is going to print file")
    void printFile() {
        PrintRequestDto printRequest = new PrintRequestDto(UUID.randomUUID(), true, 1,1, PrintRange.CUSTOM, 1, 2);
        PrintService printService = new PrintService("/usr/bin/lp");
        boolean print = printService.print("M2070", "/home/kwaaiknoffel/projects/printserver/src/test/resources/testfile_multiple_pages.pdf", printRequest);
        System.out.println("print returned: " + print);
    }
}