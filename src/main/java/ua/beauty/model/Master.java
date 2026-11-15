package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

import java.time.LocalDateTime;

/**
 * Immutable entity representing a specialist/master working at the Beauty Salon.
 * Implemented as a record (all boilerplate getters, equals, hashCode, and toString are compiler-generated).
 * Validation and normalization are executed inside the compact constructor.
 */
public record Master(
        String employeeId,
        String fullName,
        ServiceCategory category,
        LocalDateTime createdAt
) {

    public Master(String employeeId, String fullName, ServiceCategory category) {
        this(employeeId, fullName, category, LocalDateTime.now());
    }

    public Master {
        employeeId = BeautyUtils.normalize(employeeId);
        fullName = BeautyUtils.normalize(fullName);

        BeautyUtils.validateEmployeeId(employeeId);
        BeautyUtils.validateName(fullName, "Master");
        BeautyUtils.requireNotNull(category, "Master category");

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
