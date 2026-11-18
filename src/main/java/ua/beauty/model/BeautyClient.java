package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable entity representing a client in the Beauty Salon.
 * Implemented as a record (all boilerplate getters, equals, hashCode, and toString are compiler-generated).
 * Validation and normalization are executed inside the compact constructor.
 */
public record BeautyClient(
        String phone,
        String name,
        LocalDate birthDate,
        LocalDateTime createdAt
) {

    public BeautyClient(String phone, String name, LocalDate birthDate) {
        this(phone, name, birthDate, LocalDateTime.now());
    }

    public BeautyClient {
        phone = BeautyUtils.normalize(phone);
        name = BeautyUtils.normalize(name);

        BeautyUtils.validatePhone(phone);
        BeautyUtils.validateName(name, "Client");
        BeautyUtils.validateBirthDate(birthDate);

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
