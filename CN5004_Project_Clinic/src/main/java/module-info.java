/**
 * This module defines all dependencies required to run this application
 *
 * <p>
 * It includes JavaFX dependencies, opens controller packages for FXML,
 * and exports the main application packages.
 * </p>
 */

module org.example.clinic {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.jetbrains.annotations;
    requires java.desktop;

    // Open packages for JavaFX (FXML access)
    opens org.example.clinic to javafx.fxml;
    opens org.example.clinic.controllers to javafx.fxml;

    // Exported packages
    exports org.example.clinic;
    exports org.example.clinic.controllers;
    exports org.example.clinic.models.enums;
}