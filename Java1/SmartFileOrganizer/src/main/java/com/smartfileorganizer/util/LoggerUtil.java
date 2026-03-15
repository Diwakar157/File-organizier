package com.smartfileorganizer.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class for configuring and providing application-wide loggers.
 */
public class LoggerUtil {

    private static final String LOG_FILE = "smart_file_organizer.log";
    private static Logger logger;

    private LoggerUtil() {
    }

    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger("SmartFileOrganizer");
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(false);

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // File handler
            try {
                FileHandler fileHandler = new FileHandler(LOG_FILE, true);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new SimpleFormatter());
                logger.addHandler(fileHandler);
            } catch (IOException e) {
                logger.warning("Could not create log file: " + e.getMessage());
            }
        }
        return logger;
    }
}
