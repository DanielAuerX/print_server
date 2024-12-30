package com.xyz.printserver.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

class PrintServiceTest {

    @Test
    @Disabled("Is going to print file")
    void printFile() {
        PrintService printService = new PrintService();
        boolean print = printService.print("XP-452-455", "/home/kwaaiknoffel/projects/printserver/src/test/resources/testfile.pdf");
        System.out.println("print returned: " + print);
    }
}