package com.xyz.printserver.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Component
public class HouseKeepingService {

    private static final Logger log = LoggerFactory.getLogger(HouseKeepingService.class);

    private final String directoryPath;
    private final String filePrefix;

    public HouseKeepingService(
            @Value("${printserver.files.directory}") String directoryPath,
            @Value("${printserver.files.prefix}") String filePrefix) {
        this.directoryPath = directoryPath;
        this.filePrefix = filePrefix;
    }

    @Scheduled(cron = "0 0 1 * * 1-7")
    public void cleanUpFiles() {
        log.info("starting daily clean up task...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(directoryPath), filePrefix + "*")) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                    log.info("deleted: {}", file.getFileName());
                }
            }
        } catch (NoSuchFileException e) {
            log.info("the directory {} does not exist. assuming that no print request files exist", directoryPath);
        } catch (IOException e) {
            log.error("error occurred while deleting files", e);
        }
        log.info("finished clean up task");
    }

}
