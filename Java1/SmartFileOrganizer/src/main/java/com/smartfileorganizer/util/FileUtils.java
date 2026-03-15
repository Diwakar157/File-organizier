package com.smartfileorganizer.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 * Utility class for common file operations.
 */
public class FileUtils {

    private static final Logger logger = LoggerUtil.getLogger();

    private FileUtils() {
    }

    /**
     * Extracts the file extension from a file name.
     * Returns empty string if no extension is found.
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Creates a directory if it does not already exist.
     */
    public static boolean createDirectoryIfNotExists(Path dirPath) {
        if (Files.exists(dirPath)) {
            return true;
        }
        try {
            Files.createDirectories(dirPath);
            logger.info("Created directory: " + dirPath);
            return true;
        } catch (IOException e) {
            logger.severe("Failed to create directory: " + dirPath + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a unique file path to avoid overwriting existing files.
     * Appends (1), (2), etc. to the file name if a conflict exists.
     */
    public static Path getUniqueFilePath(Path destination) {
        if (!Files.exists(destination)) {
            return destination;
        }

        String fileName = destination.getFileName().toString();
        String baseName;
        String extension;

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            baseName = fileName.substring(0, lastDot);
            extension = fileName.substring(lastDot);
        } else {
            baseName = fileName;
            extension = "";
        }

        int counter = 1;
        Path parent = destination.getParent();
        Path newPath;
        do {
            newPath = parent.resolve(baseName + " (" + counter + ")" + extension);
            counter++;
        } while (Files.exists(newPath));

        return newPath;
    }

    /**
     * Computes the SHA-256 hash of a file for duplicate detection.
     */
    public static String computeFileHash(Path filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(filePath);
            byte[] hashBytes = digest.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.severe("Failed to compute hash for: " + filePath + " - " + e.getMessage());
            return null;
        }
    }
}
