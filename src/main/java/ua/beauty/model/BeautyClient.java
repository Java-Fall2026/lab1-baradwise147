package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

import java.time.LocalDate;

/**
 * Immutable entity representing a client in the Beauty Salon.
 * Implemented as a record.
 * Validation and normalization are executed inside the compact constructor.
 * All boilerplate (accessors, equals, hashCode, toString) is compiler-generated.
 */
public record BeautyClient(
        String phone,
        String name,
        LocalDate birthDate
) {

    public BeautyClient {
        phone = BeautyUtils.normalize(phone);
        name = BeautyUtils.normalize(name);

        BeautyUtils.validatePhone(phone);
        BeautyUtils.validateName(name, "Client");
        BeautyUtils.validateBirthDate(birthDate);
    }
}
