package ua.beauty;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.beauty.exception.InvalidDataException;
import ua.beauty.io.ServiceLoader;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ServiceCategory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ServiceLoader and CSV Parsing Tests")
class ServiceLoaderTest {

    private final ServiceLoader loader = new ServiceLoader();

    @Test
    @DisplayName("Should successfully parse a valid CSV line into BeautyService")
    void testParseValidLine() throws InvalidDataException {
        String line = "SRV-H01, Classic Haircut , HAIRCUT , 45 , 450.0";
        BeautyService service = loader.parseLine(line, 1);

        assertNotNull(service);
        assertEquals("SRV-H01", service.getServiceCode());
        assertEquals("Classic Haircut", service.getName());
        assertEquals(ServiceCategory.HAIRCUT, service.getCategory());
        assertEquals(45, service.getDurationMinutes());
        assertEquals(450.0, service.getPrice());
    }

    @Test
    @DisplayName("Should throw InvalidDataException with line number and cause on invalid category")
    void testParseLineInvalidCategory() {
        String line = "SRV-01,Body Wrap,SOLARIUM,60,500.0";
        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> loader.parseLine(line, 7));

        assertTrue(ex.getMessage().contains("Line 7"));
        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException on duration outside allowed range [15..240]")
    void testParseLineInvalidDuration() {
        String line = "SRV-02,Quick Fix,HAIRCUT,5,100.0";
        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> loader.parseLine(line, 12));

        assertTrue(ex.getMessage().contains("Line 12"));
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Service duration (minutes) must be between 15 and 240"));
    }

    @Test
    @DisplayName("Should throw InvalidDataException on non-positive service price")
    void testParseLineInvalidPrice() {
        String line = "SRV-03,Free Cut,HAIRCUT,30,-50.0";
        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> loader.parseLine(line, 20));

        assertTrue(ex.getMessage().contains("Line 20"));
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Service price must be greater than 0"));
    }

    @Test
    @DisplayName("Should throw InvalidDataException on malformed column count")
    void testParseLineMalformedColumnCount() {
        String line = "SRV-04,Incomplete Entry,HAIRCUT";
        InvalidDataException ex = assertThrows(InvalidDataException.class, () -> loader.parseLine(line, 3));

        assertTrue(ex.getMessage().contains("Line 3"));
        assertNotNull(ex.getCause());
        assertTrue(ex.getCause().getMessage().contains("Expected 5 columns"));
    }

    @Test
    @DisplayName("Should skip invalid lines and parse valid lines without stopping (independent of data/ files)")
    void testLoadFromReaderWithMixedLines() throws IOException {
        String csvContent = """
                serviceCode,name,category,durationMinutes,price
                SRV-01,Valid Haircut,HAIRCUT,45,400.0
                SRV-02,Invalid Category,UNKNOWN,45,400.0
                SRV-03,Valid Massage,MASSAGE,60,700.0
                SRV-04,Bad Price,FACIAL,30,-50.0
                SRV-05,Valid Manicure,MANICURE,40,350.0
                """;

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            List<BeautyService> result = loader.load(reader);
            assertEquals(3, result.size());
            assertEquals("SRV-01", result.get(0).getServiceCode());
            assertEquals("SRV-03", result.get(1).getServiceCode());
            assertEquals("SRV-05", result.get(2).getServiceCode());
        }
    }

    @Test
    @DisplayName("Should handle missing file gracefully without crashing and return empty list")
    void testLoadFromMissingFile() {
        Path missingPath = Path.of("non_existent_directory", "missing_services_file.csv");
        assertDoesNotThrow(() -> {
            List<BeautyService> result = loader.loadFromFile(missingPath);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        });
    }
}
