# Smart File Organizer

A Java desktop application that automatically organizes files based on their file types. This project provides a user-friendly GUI interface to scan directories and automatically categorize and organize files into appropriate folders.

## Project Overview

**Smart File Organizer** is a Swing-based desktop application designed to help users automatically organize files in their directories by file type. The application reads file type configurations from an XML file and intelligently categorizes files into organized folders.

## Key Features

- **Automatic File Categorization**: Scans directories and categorizes files based on predefined file type mappings
- **Configurable Categories**: File types and categories are defined in an XML configuration file (`filetypes.xml`)
- **GUI Interface**: User-friendly Swing-based graphical interface for easy interaction
- **File Organization**: Automatically moves files into appropriate category folders
- **Logging**: Built-in logging utility for tracking application behavior and debugging
- **Extensible Design**: Modular architecture makes it easy to extend and customize

## Project Structure

```
SmartFileOrganizer/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/smartfileorganizer/
│       │       ├── Main.java                    # Entry point for the application
│       │       ├── model/
│       │       │   └── FileCategory.java        # Data model for file categories
│       │       ├── service/
│       │       │   └── FileOrganizerService.java # Core service for file organization
│       │       ├── ui/
│       │       │   └── MainUI.java              # Swing GUI implementation
│       │       └── util/
│       │           ├── FileUtils.java           # File utility functions
│       │           ├── LoggerUtil.java          # Logging configuration
│       │           └── XMLParser.java           # XML parsing for configuration
│       └── resources/
│           └── filetypes.xml                    # File type configuration
├── target/                                       # Compiled classes and JAR
├── pom.xml                                       # Maven configuration
└── smart_file_organizer.log                     # Application log file
```

## Architecture

### Components

1. **Main.java**
   - Entry point for the application
   - Sets up the Swing UI with system look and feel
   - Launches the main window on the Event Dispatch Thread

2. **FileOrganizerService.java**
   - Core business logic for file scanning and organization
   - Scans directories for files
   - Categorizes files based on extension mappings
   - Handles file movement to appropriate folders
   - Provides callback functionality for progress updates

3. **MainUI.java**
   - Swing-based GUI implementation
   - Provides directory selection and file organization controls
   - Displays organized files and categories
   - User interaction handling

4. **FileCategory.java**
   - Data model representing a file category
   - Stores category name and associated file extensions

5. **Utility Classes**
   - **FileUtils.java**: File system operations and path utilities
   - **XMLParser.java**: Parses `filetypes.xml` configuration file
   - **LoggerUtil.java**: Centralized logging configuration

## Technology Stack

- **Language**: Java 17
- **Build Tool**: Apache Maven
- **GUI Framework**: Swing
- **Configuration**: XML
- **Logging**: Java Util Logging
- **Packaging**: JAR executable

## Maven Configuration

The project is built with Maven and includes:
- **Java Version**: 17
- **Maven Compiler Plugin**: For compilation
- **Maven JAR Plugin**: For packaging as executable JAR

### Key Dependencies
- No external dependencies (uses only Java standard library)

## File Type Configuration

File types and their categories are defined in `filetypes.xml`:
- Maps file extensions to categories (e.g., `.jpg`, `.png` → `Images`)
- Defines category folders for organization
- Easily extensible for adding new file types

## Building the Project

### Using Maven

```bash
# Compile the project
mvn compile

# Build the JAR
mvn package

# Run the application
java -jar target/SmartFileOrganizer-1.0-SNAPSHOT.jar
```

## How to Use

1. **Launch the Application**: Run the compiled JAR file
2. **Select Directory**: Choose a directory to organize using the file browser
3. **Review Categories**: The application displays detected file categories
4. **Organize Files**: Click the organize button to move files into category folders
5. **Monitor Progress**: Check the log file for detailed operation logs

## Logging

The application maintains a log file (`smart_file_organizer.log`) that tracks:
- Application startup and shutdown
- Files detected and categorized
- Successful file movements
- Any errors or warnings encountered

## Design Patterns

- **Service Pattern**: `FileOrganizerService` encapsulates business logic
- **Model-View Separation**: Separation between data models and UI
- **Utility Functions**: Centralized utilities for common operations
- **Callback Pattern**: Progress updates via consumer functions

## Future Enhancement Opportunities

- Support for nested directory organization
- Undo/Redo functionality
- Multi-threading for large file batches
- Configuration UI for managing file types
- Advanced filtering and search capabilities
- Batch rename functionality
- Archive handling (ZIP, RAR, etc.)

## System Requirements

- Java 17 or higher
- Operating System: Windows, macOS, or Linux
- Minimum 100MB free disk space

## License

This project is open-source and available on GitHub.

## Repository

GitHub: [https://github.com/Diwakar157/File-organizier](https://github.com/Diwakar157/File-organizier)

---

**Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026
