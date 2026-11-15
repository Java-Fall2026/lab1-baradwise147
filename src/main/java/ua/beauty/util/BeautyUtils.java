package ua.beauty.util;

import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ClientAppointment;
import ua.beauty.model.Master;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Public facade utility class for the Beauty Salon domain.
 * Exposes domain validation, normalization, and computed calculations to other packages.
 */
public final class BeautyUtils {

    public static final Set<String> ALLOWED_CATEGORIES;
    public static final Set<String> ALLOWED_STATES;

    static {
        Set<String> categories = new LinkedHashSet<>();
        categories.add("HAIRCUT");
        categories.add("MANICURE");
        categories.add("MASSAGE");
        categories.add("FACIAL");
        ALLOWED_CATEGORIES = Collections.unmodifiableSet(categories);

        Set<String> states = new LinkedHashSet<>();
        states.add("CONFIRMED");
        states.add("COMPLETED");
        states.add("CANCELLED");
        states.add("NO_SHOW");
        ALLOWED_STATES = Collections.unmodifiableSet(states);
    }

    private BeautyUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String normalize(String str) {
        return FormatHelper.normalize(str);
    }

    public static String normalizeUpper(String str) {
        return FormatHelper.normalizeUpper(str);
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    }

    public static void validatePhone(String phone) {
        ValidationHelper.requireNotBlank(phone, "Client phone");
    }

    public static void validateName(String name, String entityName) {
        ValidationHelper.requireNotBlank(name, entityName + " name");
    }

    public static void validateBirthDate(LocalDate birthDate) {
        ValidationHelper.requireMinAge(birthDate, 14, "Birth date");
    }

    public static void validateEmployeeId(String employeeId) {
        ValidationHelper.requireNotBlank(employeeId, "Master employeeId");
    }

    public static void validateCategory(String category) {
        ValidationHelper.requireNotBlank(category, "Category");
        ValidationHelper.requireAllowedValue(category, ALLOWED_CATEGORIES, "Category");
    }

    public static void validateServiceCode(String serviceCode) {
        ValidationHelper.requireNotBlank(serviceCode, "Service code");
    }

    public static void validateDurationMinutes(int durationMinutes) {
        ValidationHelper.requireInRange(durationMinutes, 15, 240, "Service duration (minutes)");
    }

    public static void validatePrice(double price) {
        ValidationHelper.requirePositive(price, "Service price");
    }

    public static void validateAppointmentState(String state) {
        ValidationHelper.requireNotBlank(state, "Appointment state");
        ValidationHelper.requireAllowedValue(state, ALLOWED_STATES, "Appointment state");
    }

    public static void validateAppointment(BeautyClient client, Master master, BeautyService service,
                                          LocalDate appointmentDate, LocalTime startTime) {
        ValidationHelper.requireNotNull(client, "Appointment client");
        ValidationHelper.requireNotNull(master, "Appointment master");
        ValidationHelper.requireNotNull(service, "Appointment service");
        ValidationHelper.requireNotNull(appointmentDate, "Appointment date");
        ValidationHelper.requireNotNull(startTime, "Appointment start time");
        ValidationHelper.requireMatchingCategories(master.getCategory(), service.getCategory());
    }

    // --- Computed methods from domain profile ---

    /**
     * Calculates the estimated end time of an appointment.
     * Formula: startTime + service.durationMinutes.
     * Not stored in a field.
     *
     * @param appointment domain appointment entity
     * @return end time of the appointment
     */
    public static LocalTime endTime(ClientAppointment appointment) {
        ValidationHelper.requireNotNull(appointment, "Appointment");
        return appointment.getStartTime().plusMinutes(appointment.getService().getDurationMinutes());
    }

    /**
     * Calculates the price per minute for a beauty service.
     * Formula: price / durationMinutes.
     * Not stored in a field.
     *
     * @param service domain service entity
     * @return price per minute in UAH
     */
    public static double pricePerMinute(BeautyService service) {
        ValidationHelper.requireNotNull(service, "Service");
        return service.getPrice() / service.getDurationMinutes();
    }
}
