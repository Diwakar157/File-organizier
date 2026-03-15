package com.smartfileorganizer.ui;

import com.smartfileorganizer.service.FileOrganizerService;
import com.smartfileorganizer.util.LoggerUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Main GUI class for the Smart File Organizer application.
 * Uses Java Swing with SwingWorker for background processing.
 */
public class MainUI extends JFrame {

    private static final Logger logger = LoggerUtil.getLogger();

    // UI Components
    private JTextField folderPathField;
    private JButton browseButton;
    private JButton organizeButton;
    private JButton detectDuplicatesButton;
    private JTextArea logArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    // Service
    private final FileOrganizerService organizerService;

    public MainUI() {
        organizerService = new FileOrganizerService();
        initializeUI();
    }

    private void initializeUI() {
        // Frame setup
        setTitle("Smart File Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setMinimumSize(new Dimension(600, 450));
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header
        JLabel headerLabel = new JLabel("Smart File Organizer", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // Folder selection panel
        JPanel folderPanel = createFolderSelectionPanel();
        centerPanel.add(folderPanel, BorderLayout.NORTH);

        // Log area
        JPanel logPanel = createLogPanel();
        centerPanel.add(logPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel (progress + status)
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createFolderSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Directory",
                TitledBorder.LEFT, TitledBorder.TOP));

        // Folder input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        JLabel folderLabel = new JLabel("Select Folder:");
        folderPathField = new JTextField();
        folderPathField.setEditable(false);
        folderPathField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        browseButton = new JButton("Browse");
        browseButton.setFocusPainted(false);

        inputRow.add(folderLabel, BorderLayout.WEST);
        inputRow.add(folderPathField, BorderLayout.CENTER);
        inputRow.add(browseButton, BorderLayout.EAST);
        panel.add(inputRow, BorderLayout.CENTER);

        // Action buttons row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        organizeButton = new JButton("Organize Files");
        organizeButton.setEnabled(false);
        organizeButton.setFocusPainted(false);
        organizeButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        detectDuplicatesButton = new JButton("Detect Duplicates");
        detectDuplicatesButton.setEnabled(false);
        detectDuplicatesButton.setFocusPainted(false);

        buttonRow.add(organizeButton);
        buttonRow.add(detectDuplicatesButton);
        panel.add(buttonRow, BorderLayout.SOUTH);

        // Event handlers
        browseButton.addActionListener(this::onBrowse);
        organizeButton.addActionListener(this::onOrganize);
        detectDuplicatesButton.addActionListener(this::onDetectDuplicates);

        return panel;
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Log Output",
                TitledBorder.LEFT, TitledBorder.TOP));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Clear button
        JButton clearButton = new JButton("Clear Log");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> logArea.setText(""));
        JPanel clearPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        clearPanel.add(clearButton);
        panel.add(clearPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5, 0, 0, 0));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("");
        panel.add(progressBar, BorderLayout.CENTER);

        statusLabel = new JLabel("Status: Waiting for user input");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    // ========== Event Handlers ==========

    private void onBrowse(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Folder to Organize");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            folderPathField.setText(path);
            organizeButton.setEnabled(true);
            detectDuplicatesButton.setEnabled(true);
            statusLabel.setText("Status: Folder selected - " + path);
            appendLog("Selected folder: " + path);
            logger.info("Folder selected: " + path);
        }
    }

    private void onOrganize(ActionEvent e) {
        String folderPath = folderPathField.getText().trim();
        if (folderPath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a folder first.", "No Folder Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "This will organize all files in:\n" + folderPath +
                        "\n\nFiles will be moved into category subfolders.\nContinue?",
                "Confirm Organization", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setControlsEnabled(false);
        logArea.setText("");
        progressBar.setIndeterminate(true);
        progressBar.setString("Organizing...");
        statusLabel.setText("Status: Organizing files...");

        // Run in background thread using SwingWorker
        SwingWorker<Map<String, Integer>, String> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Integer> doInBackground() throws Exception {
                return organizerService.organizeFiles(
                        Path.of(folderPath),
                        message -> publish(message)
                );
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    appendLog(message);
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                setControlsEnabled(true);

                try {
                    Map<String, Integer> stats = get();
                    int totalMoved = stats.values().stream()
                            .mapToInt(Integer::intValue).sum();
                    progressBar.setValue(100);
                    progressBar.setString("Done - " + totalMoved + " file(s) organized");
                    statusLabel.setText("Status: Organization complete");
                    logger.info("Organization completed. Total files moved: " + totalMoved);
                } catch (Exception ex) {
                    progressBar.setString("Error");
                    statusLabel.setText("Status: Error occurred");
                    appendLog("ERROR: " + ex.getMessage());
                    logger.severe("Organization failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(MainUI.this,
                            "An error occurred:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void onDetectDuplicates(ActionEvent e) {
        String folderPath = folderPathField.getText().trim();
        if (folderPath.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select a folder first.", "No Folder Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setControlsEnabled(false);
        progressBar.setIndeterminate(true);
        progressBar.setString("Scanning for duplicates...");
        statusLabel.setText("Status: Detecting duplicates...");

        SwingWorker<Map<String, List<Path>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, List<Path>> doInBackground() throws Exception {
                return organizerService.detectDuplicates(Path.of(folderPath));
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                setControlsEnabled(true);

                try {
                    Map<String, List<Path>> duplicates = get();

                    if (duplicates.isEmpty()) {
                        appendLog("\n--- No duplicate files found ---");
                        progressBar.setString("No duplicates found");
                    } else {
                        appendLog("\n--- Duplicate Files Detected ---");
                        int groupCount = 0;
                        for (Map.Entry<String, List<Path>> entry : duplicates.entrySet()) {
                            groupCount++;
                            appendLog("\nDuplicate Group " + groupCount + ":");
                            for (Path file : entry.getValue()) {
                                appendLog("  " + file.getFileName());
                            }
                        }
                        appendLog("\nTotal duplicate groups: " + groupCount);
                        progressBar.setString(groupCount + " duplicate group(s) found");
                    }

                    statusLabel.setText("Status: Duplicate detection complete");

                } catch (Exception ex) {
                    progressBar.setString("Error");
                    statusLabel.setText("Status: Error occurred");
                    appendLog("ERROR: " + ex.getMessage());
                    logger.severe("Duplicate detection failed: " + ex.getMessage());
                }
            }
        };

        worker.execute();
    }

    // ========== Utility Methods ==========

    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void setControlsEnabled(boolean enabled) {
        browseButton.setEnabled(enabled);
        organizeButton.setEnabled(enabled);
        detectDuplicatesButton.setEnabled(enabled);
    }
}
