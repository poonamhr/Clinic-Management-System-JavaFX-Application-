package org.example.clinic.common;

import java.util.regex.Pattern;

/**
 * This class provides validation methods for user input fields.
 *
 * <p>
 * It centralizes input validation logic to ensure consistency
 * and data integrity across the application.
 * </p>
 *
 * <p>
 * All validation methods throw {@link IllegalArgumentException}
 * when the input is invalid.
 * </p>
 */

public final class ValidationUtils {

    /**
     * Precompiled regular expression pattern for validating email addresses.
     */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Private constructor to prevent instantiation.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates that a name is not null or empty.
     *
     * @param name      input value
     * @param fieldName field name for error messages
     */
    public static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    /**
     * Validates a 10-digit phone number
     *
     * @param phone input value
     */
    public static void validatePhone(String phone) {
        if (phone == null || !phone.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone must be exactly 10 digits");
        }
    }

    /**
     * Validates email format
     *
     * @param email input value
     */
    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates tax ID
     *
     * @param taxId input value
     */
    public static void validateTaxId(String taxId) {
        if (taxId == null || taxId.length() < 5) {
            throw new IllegalArgumentException("Tax ID must be at least 5 characters");
        }
    }

    /**
     * Validates that a required field is not null.

     * @param obj input value
     * @param fieldName field name for error messages
     */
    public static void validateRequired(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}