package ua.beauty.model;

import ua.beauty.util.BeautyUtils;
import ua.common.BaseEntity;

import java.util.Objects;

/**
 * Entity representing a service provided by the Beauty Salon.
 * Key field: {@code serviceCode} (🔑).
 * Creation is performed via static factory {@link #of(String, String, String, int, double)}.
 * Has a mutable field: {@code price} (✎).
 */
public final class BeautyService extends BaseEntity {

    private final String serviceCode;
    private final String name;
    private final String category;
    private final int durationMinutes;
    private double price;

    private BeautyService(String serviceCode, String name, String category, int durationMinutes, double price) {
        super();
        String normalizedCode = BeautyUtils.normalize(serviceCode);
        String normalizedName = BeautyUtils.normalize(name);
        String normalizedCat = BeautyUtils.normalizeUpper(category);

        BeautyUtils.validateServiceCode(normalizedCode);
        BeautyUtils.validateName(normalizedName, "Service");
        BeautyUtils.validateCategory(normalizedCat);
        BeautyUtils.validateDurationMinutes(durationMinutes);

        this.serviceCode = normalizedCode;
        this.name = normalizedName;
        this.category = normalizedCat;
        this.durationMinutes = durationMinutes;

        // Setter is called to avoid duplicate validation logic
        setPrice(price);
    }

    public static BeautyService of(String serviceCode, String name, String category, int durationMinutes, double price) {
        return new BeautyService(serviceCode, name, category, durationMinutes, price);
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    /**
     * Sets a new price for the service.
     * The setter is marked final because it is invoked from the constructor.
     *
     * @param price new price, strictly greater than 0
     */
    public final void setPrice(double price) {
        BeautyUtils.validatePrice(price);
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BeautyService that = (BeautyService) o;
        return Objects.equals(serviceCode, that.serviceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceCode);
    }

    @Override
    public String toString() {
        return "BeautyService{" +
                "serviceCode='" + serviceCode + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", durationMinutes=" + durationMinutes +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }
}
