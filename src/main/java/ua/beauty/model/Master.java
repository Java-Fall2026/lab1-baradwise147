package ua.beauty.model;

import ua.beauty.util.BeautyUtils;
import ua.common.BaseEntity;

import java.util.Objects;

/**
 * Entity representing a specialist/master working at the Beauty Salon.
 * Key field: {@code employeeId} (🔑).
 */
public final class Master extends BaseEntity {

    private final String employeeId;
    private final String fullName;
    private final String category;

    public Master(String employeeId, String fullName, String category) {
        super();
        String normalizedId = BeautyUtils.normalize(employeeId);
        String normalizedName = BeautyUtils.normalize(fullName);
        String normalizedCat = BeautyUtils.normalizeUpper(category);

        BeautyUtils.validateEmployeeId(normalizedId);
        BeautyUtils.validateName(normalizedName, "Master");
        BeautyUtils.validateCategory(normalizedCat);

        this.employeeId = normalizedId;
        this.fullName = normalizedName;
        this.category = normalizedCat;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Master master = (Master) o;
        return Objects.equals(employeeId, master.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    @Override
    public String toString() {
        return "Master{" +
                "employeeId='" + employeeId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
