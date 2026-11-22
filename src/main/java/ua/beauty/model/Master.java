package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

/**
 * Immutable entity representing a specialist/master working at the Beauty Salon.
 * Implemented as a record.
 * Validation and normalization are executed inside the compact constructor.
 */
public record Master(
        String employeeId,
        String fullName,
        ServiceCategory category
) {

    public Master {
        employeeId = BeautyUtils.normalize(employeeId);
        fullName = BeautyUtils.normalize(fullName);

        BeautyUtils.validateEmployeeId(employeeId);
        BeautyUtils.validateName(fullName, "Master");
        BeautyUtils.requireNotNull(category, "Master category");
    }
}
