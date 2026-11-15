package ua.beauty.util;

import java.util.Locale;

/**
 * Package-private helper providing formatting and normalization utilities.
 */
final class FormatHelper {

    private FormatHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static String normalize(String str) {
        return str == null ? null : str.trim();
    }

    static String normalizeUpper(String str) {
        return str == null ? null : str.trim().toUpperCase(Locale.ROOT);
    }

    static String capitalize(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        String trimmed = str.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1).toLowerCase(Locale.ROOT);
    }

    static String formatMoney(double amount) {
        return String.format(Locale.US, "%.2f UAH", amount);
    }
}
