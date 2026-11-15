package ua.beauty.model;

/**
 * Category of salon services and master qualifications.
 * Each constant encapsulates a price factor multiplier.
 */
public enum ServiceCategory {
    HAIRCUT(1.0),
    MANICURE(1.2),
    MASSAGE(1.8),
    FACIAL(1.5);

    private final double priceFactor;

    ServiceCategory(double priceFactor) {
        this.priceFactor = priceFactor;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}
