package org.example.clinic.common;

import java.util.Locale;

import org.jetbrains.annotations.NotNull;

/**
 * This class provides common helper methods used across the application. It includes:
 * <ul>
 *     <li>Operating system detection utilities</li>
 *     <li>String normalization and formatting helpers</li>
 * </ul>
 * <p> This class is static and cannot be instantiated.</p>
 */

public final class Utils {

    /**
     * Private constructor to prevent instantiation.
     */
    private Utils() {
    }

    /**
     * Capitalizes the first letter of each word.
     * <p>
     * The input string is first normalized to lowercase, then each word
     * is transformed such that its first character is uppercase and
     * the remaining characters are lowercase.
     * </p>
     *
     * @param s the input string (must not be {@code null})
     * @return capitalised string
     */
    public static @NotNull String capitalise(@NotNull String s) {
        if (s.isEmpty()) return s;

        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            word = normaliseLower(word);

            sb.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }

    /**
     * Trims and converts a string to lowercase.
     *
     * @param s the input string (must not be {@code null})
     * @return normalised string
     */

    public static @NotNull String normaliseLower(@NotNull String s) {
        return s.trim().toLowerCase(Locale.getDefault());
    }
}