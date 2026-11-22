package ua.beauty.util;

import ua.beauty.model.AppointmentState;
import ua.beauty.model.BeautyClient;
import ua.beauty.model.BeautyService;
import ua.beauty.model.ClientAppointment;
import ua.beauty.model.Master;
import ua.beauty.model.ServiceCategory;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Public facade utility class for the Beauty Salon domain.
 * Exposes validation routines, formatting, computed domain methods, and switch expressions.
 */
public final class BeautyUtils {

    private BeautyUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String normalize(String str) {
        return FormatHelper.normalize(str);
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    }

    public static void requireNotNull(Object value, String fieldName) {
        ValidationHelper.requireNotNull(value, fieldName);
    }

    public static void requireNotBlank(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
    }

    public static void requirePositive(double value, String fieldName) {
        ValidationHelper.requirePositive(value, fieldName);
    }

    public static void requirePositive(int value, String fieldName) {
        ValidationHelper.requirePositive(value, fieldName);
    }

    public static void requireInRange(int value, int min, int max, String fieldName) {
        ValidationHelper.requireInRange(value, min, max, fieldName);
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

    public static void validateServiceCode(String serviceCode) {
        ValidationHelper.requireNotBlank(serviceCode, "Service code");
    }

    public static void validateDurationMinutes(int durationMinutes) {
        ValidationHelper.requireInRange(durationMinutes, 15, 240, "Service duration (minutes)");
    }

    public static void validatePrice(double price) {
        ValidationHelper.requirePositive(price, "Service price");
    }

    public static void validateAppointment(BeautyClient client, Master master, BeautyService service,
                                          LocalDate appointmentDate, LocalTime startTime, AppointmentState state) {
        ValidationHelper.requireNotNull(client, "Appointment client");
        ValidationHelper.requireNotNull(master, "Appointment master");
        ValidationHelper.requireNotNull(service, "Appointment service");
        ValidationHelper.requireNotNull(appointmentDate, "Appointment date");
        ValidationHelper.requireNotNull(startTime, "Appointment start time");
        ValidationHelper.requireNotNull(state, "Appointment state");
        ValidationHelper.requireMatchingCategories(master.category(), service.getCategory());
    }

    // --- Computed methods from domain profile ---

    /**
     * Calculates the estimated end time of an appointment.
     * Formula: startTime + service.durationMinutes.
     * Not stored in a field.
     */
    public static LocalTime endTime(ClientAppointment appointment) {
        ValidationHelper.requireNotNull(appointment, "Appointment");
        return appointment.getStartTime().plusMinutes(appointment.getService().getDurationMinutes());
    }

    /**
     * Calculates the price per minute for a beauty service.
     * Formula: price / durationMinutes.
     * Not stored in a field.
     */
    public static double pricePerMinute(BeautyService service) {
        ValidationHelper.requireNotNull(service, "Service");
        return service.getPrice() / service.getDurationMinutes();
    }

    // --- Switch Expressions (Java 14+) ---

    /**
     * Switch-expression #1: Exhaustive check on ServiceCategory enum WITHOUT default.
     * Maps each category to a detailed domain description.
     */
    public static String getCategoryDescription(ServiceCategory category) {
        ValidationHelper.requireNotNull(category, "Category");
        return switch (category) {
            case HAIRCUT -> "Hair styling, cutting and precision grooming services";
            case MANICURE -> "Nail aesthetic care, polish styling and hand therapy";
            case MASSAGE -> "Therapeutic relaxation and deep muscle recovery";
            case FACIAL -> "Cosmetic facial cleansing, hydration and skincare therapy";
        };
    }

    /**
     * Switch-expression #2: Domain behavior dispatch based on appointment state.
     * Determines the operational salon workflow action for the current state.
     */
    public static String getNextAction(AppointmentState state) {
        ValidationHelper.requireNotNull(state, "Appointment state");
        return switch (state) {
            case CONFIRMED -> "Send automated SMS reminder and prepare master workstation";
            case COMPLETED -> "Issue official receipt, credit loyalty points, and request service review";
            case CANCELLED -> "Release booked time slot immediately and update master schedule";
            case NO_SHOW -> "Record missed visit penalty in client account profile";
        };
    }
}
