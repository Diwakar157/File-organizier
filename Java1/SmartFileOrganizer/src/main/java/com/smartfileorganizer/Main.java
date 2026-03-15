package com.smartfileorganizer;

import com.smartfileorganizer.ui.MainUI;
import com.smartfileorganizer.util.LoggerUtil;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * Entry point for the Smart File Organizer application.
 */
public class Main {

    private static final Logger logger = LoggerUtil.getLogger();

    public static void main(String[] args) {
        logger.info("Starting Smart File Organizer...");

        // Set system look and feel for native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warning("Could not set system look and feel: " + e.getMessage());
        }

        // Launch GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainUI mainWindow = new MainUI();
            mainWindow.setVisible(true);
            logger.info("Application UI launched successfully");
        });
    }
}
