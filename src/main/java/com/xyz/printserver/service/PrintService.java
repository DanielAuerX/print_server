package com.xyz.printserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PrintService {

    private static final Logger log = LoggerFactory.getLogger(PrintService.class);

    //todo refactor
    public boolean print(String printer, String fileLocation) {
        if (printer == null || printer.isEmpty() || fileLocation == null || fileLocation.isEmpty()) {
            log.error("printer name or file location is invalid.");
            return false;
        }

        AtomicBoolean isPrintSuccessful = new AtomicBoolean(false);

        try {
            Process process = new ProcessBuilder("/usr/bin/lp", "-d", printer, fileLocation).start();

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
}
