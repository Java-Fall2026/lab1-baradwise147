package ua.beauty.util;

import ua.beauty.model.ServiceCategory;

import java.time.LocalDate;
import java.time.Period;

/**
 * Package-private helper providing low-level validation routines.
 * Throws {@link IllegalArgumentException} with clear descriptive messages showing expected and actual values.
 */
final class ValidationHelper {

    private ValidationHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
    }

    static void requireNotBlank(String value, String fieldName) {
        requireNotNull(value, fieldName);
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank, got: '" + value + "'");
        }
    }

    static void requirePositive(double value, String fieldName) {
        if (Double.isNaN(value) || Double.isInfinite(value) || value <= 0.0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0, got: " + value);
        }
    }

    static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0, got: " + value);
        }
    }

    static void requireInRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                    fieldName + " must be between " + min + " and " + max + ", got: " + value);
        }
    }

    static void requireMinAge(LocalDate birthDate, int minAgeYears, String fieldName) {
        requireNotNull(birthDate, fieldName);
        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            throw new IllegalArgumentException(
                    fieldName + " cannot be in the future, got: " + birthDate);
        }
        int age = Period.between(birthDate, today).getYears();
        if (age < minAgeYears) {
            throw new IllegalArgumentException(
                    "Client age must be at least " + minAgeYears + " years old, got: " + age + " (birth date: " + birthDate + ")");
        }
    }

    static void requireMatchingCategories(ServiceCategory masterCategory, ServiceCategory serviceCategory) {
        requireNotNull(masterCategory, "Master category");
        requireNotNull(serviceCategory, "Service category");
        if (masterCategory != serviceCategory) {
            throw new IllegalArgumentException(
                    "Master category must match service category. Master has '" + masterCategory
                            + "', but service requires '" + serviceCategory + "'");
        }
    }
}
