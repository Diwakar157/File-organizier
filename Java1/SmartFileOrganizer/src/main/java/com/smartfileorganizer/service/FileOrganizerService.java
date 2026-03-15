package com.smartfileorganizer.service;

import com.smartfileorganizer.model.FileCategory;
import com.smartfileorganizer.util.FileUtils;
import com.smartfileorganizer.util.LoggerUtil;
import com.smartfileorganizer.util.XMLParser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Core service class that handles file scanning, categorization, and organization.
 */
public class FileOrganizerService {

    private static final Logger logger = LoggerUtil.getLogger();
    private static final String UNCATEGORIZED_FOLDER = "Others";

    private final Map<String, String> extensionMap;
    private final List<FileCategory> categories;

    public FileOrganizerService() {
        this.categories = XMLParser.parseCategories();
        this.extensionMap = XMLParser.buildExtensionMap(categories);
    }

    /**
     * Scans the given directory and returns a list of regular files (not directories).
     */
    public List<Path> scanDirectory(Path directory) throws IOException {
        List<Path> files = new ArrayList<>();

        if (!Files.isDirectory(directory)) {
            throw new IOException("Invalid directory: " + directory);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    files.add(entry);
                    logger.info("File detected: " + entry.getFileName());
                }
            }
        }

        logger.info("Total files found: " + files.size());
        return files;
    }

    /**
     * Determines the category for a given file based on its extension.
     */
    public String getCategory(Path file) {
        String extension = FileUtils.getFileExtension(file.getFileName().toString());
        if (extension.isEmpty()) {
            return UNCATEGORIZED_FOLDER;
        }
        return extensionMap.getOrDefault(extension, UNCATEGORIZED_FOLDER);
    }

    /**
     * Moves a single file to its category folder within the base directory.
     * Returns a log message describing what happened.
     */
    public String moveFile(Path file, Path baseDirectory) throws IOException {
        String category = getCategory(file);

        Path categoryDir = baseDirectory.resolve(category);
        FileUtils.createDirectoryIfNotExists(categoryDir);

        Path destination = categoryDir.resolve(file.getFileName());
        destination = FileUtils.getUniqueFilePath(destination);

        Files.move(file, destination, StandardCopyOption.ATOMIC_MOVE);

        String message = file.getFileName() + " -> " + category + "/";
        logger.info("File moved: " + message);
        return message;
    }

    /**
     * Organizes all files in the given directory.
     *
     * @param directory       the directory to organize
     * @param progressCallback called after each file is processed with a status message
     * @return statistics map (category name -> file count)
     */
    public Map<String, Integer> organizeFiles(Path directory, Consumer<String> progressCallback)
            throws IOException {

        List<Path> files = scanDirectory(directory);
        Map<String, Integer> statistics = new HashMap<>();

        if (files.isEmpty()) {
            progressCallback.accept("No files found in the selected directory.");
            return statistics;
        }

        progressCallback.accept("Found " + files.size() + " file(s) to organize.\n");

        int processed = 0;
        int errors = 0;

        for (Path file : files) {
            try {
                String result = moveFile(file, directory);
                progressCallback.accept("[" + (processed + 1) + "/" + files.size() + "] " + result);

                String category = getCategory(file);
                statistics.merge(category, 1, Integer::sum);
                processed++;

            } catch (IOException e) {
                errors++;
                String errorMsg = "Failed to move " + file.getFileName() + ": " + e.getMessage();
                logger.severe(errorMsg);
                progressCallback.accept("ERROR: " + errorMsg);
            }
        }

        // Summary
        progressCallback.accept("\n--- Organization Complete ---");
        progressCallback.accept("Files organized: " + processed);
        if (errors > 0) {
            progressCallback.accept("Errors: " + errors);
        }

        // Statistics breakdown
        progressCallback.accept("\n--- Statistics ---");
        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            progressCallback.accept(entry.getKey() + ": " + entry.getValue() + " file(s)");
        }

        return statistics;
    }

    /**
     * Detects duplicate files in the given directory using SHA-256 hashing.
     */
    public Map<String, List<Path>> detectDuplicates(Path directory) throws IOException {
        List<Path> files = scanDirectory(directory);
        Map<String, List<Path>> hashMap = new HashMap<>();

        for (Path file : files) {
            String hash = FileUtils.computeFileHash(file);
            if (hash != null) {
                hashMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(file);
            }
        }

        // Filter to only groups with duplicates
        Map<String, List<Path>> duplicates = new HashMap<>();
        for (Map.Entry<String, List<Path>> entry : hashMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicates.put(entry.getKey(), entry.getValue());
            }
        }

        return duplicates;
    }

    public List<FileCategory> getCategories() {
        return categories;
    }

    public Map<String, String> getExtensionMap() {
        return extensionMap;
    }
}
