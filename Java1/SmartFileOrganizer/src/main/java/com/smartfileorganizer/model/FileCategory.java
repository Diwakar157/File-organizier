package com.smartfileorganizer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a file category with a name and associated file extensions.
 */
public class FileCategory {

    private final String name;
    private final List<String> extensions;

    public FileCategory(String name) {
        this.name = name;
        this.extensions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }

    public void addExtension(String extension) {
        extensions.add(extension.toLowerCase());
    }

    public boolean matchesExtension(String extension) {
        return extensions.contains(extension.toLowerCase());
    }

    @Override
    public String toString() {
        return name + " " + extensions;
    }
}
