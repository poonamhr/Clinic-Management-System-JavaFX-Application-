package org.example.clinic.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for file-based persistence operations.
 *
 * <p>
 * It provides I/O functionality for reading and writing
 * data to the local file system. It is primarily used for CSV-based storage.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Saving structured text data to files</li>
 *     <li>Loading data from files into memory</li>
 *     <li>Ensuring required directories exist before file operations</li>
 * </ul>
 * </p>
 *
 * <p>
 * All methods are static and stateless, making this class a reusable
 * persistence utility.
 * </p>
 */
public class FileManager {

    /**
     * Writes a list of lines to a file.
     *
     * @param filePath file path
     * @param lines    data to write
     * @throws RuntimeException if an I/O error occurs during writing
     */
    public static void saveLines(String filePath, List<String> lines) {
        try {
            File file = new File(filePath);
            File parent = file.getParentFile();

            // Ensure parent directory exists
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    System.err.println("Failed to create directory: " + parent);
                }
            }

            // Write lines to file (overwrite mode)
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.println(line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error saving file: " + filePath);
            throw new RuntimeException("Export failed", e);
        }
    }

    /**
     * Ensures that a directory exists.
     * <p> If the directory does not exist, it will be created recursively. </p>
     *
     * @param path the directory path to check or create
     */
    public static void ensureDirectoryExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Loads all lines from a file
     * <p> If the file does not exist, an empty list is returned.
     *
     * @param filePath file path
     * @return list of lines (empty if file not found)
     * @throws RuntimeException if an I/O error occurs during reading
     */
    public static List<String> loadLines(String filePath) {
        List<String> lines = new ArrayList<>();

        try {
            File file = new File(filePath);

            // Return empty list if file does not exist
            if (!file.exists()) return lines;

            // Read file line by line
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error loading file: " + filePath);
            throw new RuntimeException("Export failed", e);
        }

        return lines;
    }
}