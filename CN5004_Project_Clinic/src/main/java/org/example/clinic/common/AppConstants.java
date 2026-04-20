package org.example.clinic.common;

/**
 * This class stores application-wide constant values.
 *
 * <p>
 * It defines immutable values used across the application, including:
 * <ul>
 *     <li>User interface dimensions</li>
 *     <li>Resource directory paths (FXML, CSS, fonts)</li>
 *     <li>Data storage locations and file names</li>
 * </ul>
 * </p>
 *
 * <p>
 * The purpose of this class is to:
 * <ul>
 *     <li>Eliminate hard-coded values throughout the codebase</li>
 *     <li>Improve maintainability and consistency</li>
 *     <li>Provide a single source for configuration values</li>
 * </ul>
 *</p>
 *
 * <p> All constants are declared as {@code public static final} and should not be modified at runtime. </p>
 */
public final class AppConstants {

    // Private constructor to prevent instantiation
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Window size
    public static final double WINDOW_WIDTH = 1150;
    public static final double WINDOW_HEIGHT = 680;

    // Resource paths
    public static final String FXML_BASE_PATH = "/org/example/clinic/views/";
    public static final String CSS_BASE_PATH = "/org/example/clinic/css/";

    // Root directory for data storage.
    public static final String DATA_PATH = "data/";

    // File path for storing doctor/patient/appointment records in CSV format.
    public static final String DOCTORS_FILE = DATA_PATH + "doctors.csv";
    public static final String PATIENTS_FILE = DATA_PATH + "patients.csv";
    public static final String APPOINTMENTS_FILE = DATA_PATH + "appointments.csv";
}