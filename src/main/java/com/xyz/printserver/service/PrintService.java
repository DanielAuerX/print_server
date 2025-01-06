package com.xyz.printserver.service;

import com.xyz.printserver.exception.InvalidPrintSettings;
import com.xyz.printserver.model.PrintRange;
import com.xyz.printserver.model.PrintRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PrintService {

    private final String lPpath;

    private static final Logger log = LoggerFactory.getLogger(PrintService.class);
    private static final int MAX_COPIES = 10;
    private static final int MAX_PAGES_PER_SIDE = 4;

    public PrintService(@Value("${printserver.lp.path}") String lPpath) {
        this.lPpath = lPpath;
    }

    //todo refactor
    public boolean print(String printer, String fileLocation, PrintRequestDto printRequest) {
        if (printer == null || printer.isEmpty() || fileLocation == null || fileLocation.isEmpty()) {
            log.error("printer name or file location is invalid.");
            return false;
        }

        boolean isDefault = checkIfDefault(printRequest);
        if (!isDefault) validatePrintSettings(printRequest);

        AtomicBoolean isPrintSuccessful = new AtomicBoolean(false);

        try {
            ProcessBuilder processBuilder = buildProcess(printer, fileLocation, printRequest, isDefault);
            Process process = processBuilder.start();

            CompletableFuture<Void> stdOutFuture = CompletableFuture.runAsync(() -> {
                try (BufferedReader stdOutput = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    stdOutput.lines().forEach(line -> {
                        log.info("STDOUT: {}", line);
                        synchronized (this) {
                            if (line.contains("request id is " + printer)) {
                                isPrintSuccessful.set(true);
                            }
                        }

                    });
                } catch (IOException e) {
                    log.error("error reading standard output", e);
                }
            });

            CompletableFuture<Void> stdErrFuture = CompletableFuture.runAsync(() -> {
                try (BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    stdError.lines().forEach(line -> log.error("STDERR: {}", line));
                } catch (IOException e) {
                    log.error("error reading standard error", e);
                }
            });

            int exitCode = process.waitFor();
            CompletableFuture.allOf(stdOutFuture, stdErrFuture).join();

            log.info("print process exited with code: {}", exitCode);
            return isPrintSuccessful.get() && exitCode == 0;

        } catch (IOException | InterruptedException e) {
            log.error("failed to print", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private ProcessBuilder buildProcess(String printer, String fileLocation, PrintRequestDto printRequest, boolean isDefault) {
        if (isDefault) {
            return new ProcessBuilder(lPpath, "-d", printer, fileLocation);
        } else if (printRequest.printRange() == PrintRange.ALL_PAGES) {
            return new ProcessBuilder(lPpath,
                    "-n", String.valueOf(printRequest.numberOfCopies()),
                    "-o", "number-up=" + printRequest.pagesPerSide(),
                    "-d", printer, fileLocation);
        } else {
            return new ProcessBuilder(lPpath,
                    "-n", String.valueOf(printRequest.numberOfCopies()),
                    "-o", "number-up=" + printRequest.pagesPerSide(),
                    "-o", "page-ranges=" + printRequest.rangeStart().toString() + "-" + printRequest.rangeEnd().toString(),
                    "-d", printer, fileLocation);
        }
    }

    private boolean checkIfDefault(PrintRequestDto printRequest) {
        boolean isDefault = printRequest.numberOfCopies() == 1;
        if (printRequest.pagesPerSide() != 1) isDefault = false;
        if (printRequest.printRange() != PrintRange.ALL_PAGES) isDefault = false;
        return isDefault;
    }

    private void validatePrintSettings(PrintRequestDto printRequest) {
        if (printRequest.numberOfCopies() < 1 || printRequest.numberOfCopies() > MAX_COPIES) {
            throw new InvalidPrintSettings("invalid number of copies: " + printRequest.numberOfCopies());
        }
        if (printRequest.pagesPerSide() < 1 || printRequest.pagesPerSide() > MAX_PAGES_PER_SIDE) {
            throw new InvalidPrintSettings("invalid pages per side: " + printRequest.pagesPerSide());
        }
        if (printRequest.printRange() == PrintRange.CUSTOM) {
            if (printRequest.rangeStart() == null || printRequest.rangeStart() < 1 ||
                    printRequest.rangeEnd() == null || printRequest.rangeEnd() < printRequest.rangeStart()) {
                throw new InvalidPrintSettings("invalid print range: " + printRequest.rangeStart() + "-" + printRequest.rangeEnd());
            }
        }
    }
}
