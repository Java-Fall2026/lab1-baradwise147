package ua.beauty.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.beauty.exception.InvalidDataException;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ServiceCategory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Service loader that reads and parses BeautyService records from CSV files or readers.
 * Handles corrupted lines without stopping execution, utilizing try-with-resources,
 * multi-catch, finally, and SLF4J logging across INFO, WARN, and ERROR levels.
 */
public class ServiceLoader {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoader.class);

    /**
     * Parses a single CSV line into a BeautyService instance.
     *
     * @param line       raw CSV text line
     * @param lineNumber 1-based line number for error reporting
     * @return parsed BeautyService
     * @throws InvalidDataException if the line is malformed or violates business constraints
     */
    public BeautyService parseLine(String line, int lineNumber) throws InvalidDataException {
        if (line == null || line.isBlank()) {
            throw new InvalidDataException("Line " + lineNumber + ": line is empty or null");
        }

        String[] parts = line.split(",", -1);

        try {
            if (parts.length != 5) {
                throw new IllegalArgumentException("Expected 5 columns, got: " + parts.length);
            }

            String code = parts[0].trim();
            String name = parts[1].trim();
            ServiceCategory category = ServiceCategory.valueOf(parts[2].trim().toUpperCase(Locale.ROOT));
            int durationMinutes = Integer.parseInt(parts[3].trim());
            double price = Double.parseDouble(parts[4].trim());

            return new BeautyService(code, name, category, durationMinutes, price);
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    /**
     * Reads services from a Reader stream.
     * Invalid lines are converted to InvalidDataException, logged as WARN, and skipped.
     *
     * @param reader reader providing CSV data
     * @return list of successfully parsed services
     * @throws IOException if a low-level I/O error occurs
     */
    public List<BeautyService> load(BufferedReader reader) throws IOException {
        List<BeautyService> services = new ArrayList<>();
        int lineNumber = 0;
        int skippedCount = 0;

        String header = reader.readLine();
        lineNumber++;

        if (header == null) {
            log.warn("Encountered empty CSV source");
            return services;
        }

        String line;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (line.isBlank()) {
                continue;
            }
            try {
                BeautyService service = parseLine(line, lineNumber);
                services.add(service);
            } catch (InvalidDataException e) {
                skippedCount++;
                log.warn("Skipping invalid record at line {}: {}", lineNumber, e.getMessage());
            }
        }

        log.info("Parsed {} valid services, skipped {} corrupted lines", services.size(), skippedCount);
        return services;
    }

    /**
     * Reads services from the specified file path.
     * Uses try-with-resources for stream management, multi-catch for error classification,
     * and finally for audit logging. If the file is missing, logs ERROR and returns an empty list.
     *
     * @param path path to the CSV file
     * @return list of valid services
     */
    public List<BeautyService> loadFromFile(Path path) {
        if (path == null) {
            log.error("File path cannot be null");
            return Collections.emptyList();
        }

        List<BeautyService> services = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        log.info("Starting to read services from: {}", path);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            services = load(reader);
            successCount = services.size();
        } catch (NoSuchFileException | FileNotFoundException e) {
            failureCount++;
            log.error("Requested file does not exist at path: {}", path, e);
        } catch (IOException e) {
            failureCount++;
            log.error("I/O error occurred while reading file: {}", path, e);
        } finally {
            log.info("Finished processing file: {}. Loaded items: {}, I/O failures: {}",
                    path, successCount, failureCount);
        }

        return services;
    }
}
