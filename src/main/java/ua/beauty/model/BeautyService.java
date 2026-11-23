package ua.beauty.model;

import ua.beauty.util.BeautyUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mutable entity representing a service provided by the Beauty Salon.
 * Remains a class because {@code price} is mutable (✎).
 */
public final class BeautyService {

    private final String serviceCode;
    private final String name;
    private final ServiceCategory category;
    private final int durationMinutes;
    private final LocalDateTime createdAt;
    private double price;

    public BeautyService(String serviceCode, String name, ServiceCategory category, int durationMinutes, double price) {
        String normalizedCode = BeautyUtils.normalize(serviceCode);
        String normalizedName = BeautyUtils.normalize(name);

        BeautyUtils.validateServiceCode(normalizedCode);
        BeautyUtils.validateName(normalizedName, "Service");
        BeautyUtils.requireNotNull(category, "Service category");
        BeautyUtils.validateDurationMinutes(durationMinutes);

        this.serviceCode = normalizedCode;
        this.name = normalizedName;
        this.category = category;
        this.durationMinutes = durationMinutes;
        this.createdAt = LocalDateTime.now();

        setPrice(price);
    }

    public static BeautyService of(String serviceCode, String name, ServiceCategory category, int durationMinutes, double price) {
        return new BeautyService(serviceCode, name, category, durationMinutes, price);
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getName() {
        return name;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets a new price for the service.
     *
     * @param price new price, strictly greater than 0
     */
    public final void setPrice(double price) {
        BeautyUtils.requirePositive(price, "Service price");
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
                ", category=" + category +
                ", durationMinutes=" + durationMinutes +
                ", price=" + price +
                ", createdAt=" + createdAt +
                '}';
    }
}
