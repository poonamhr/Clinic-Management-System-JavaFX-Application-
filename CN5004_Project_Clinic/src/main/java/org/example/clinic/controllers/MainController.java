package org.example.clinic.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.example.clinic.App;

/**
 * Controller responsible for managing the main application view and navigation.
 *
 * <p>
 * Acts as the central navigation hub of the Clinic Management System,
 * handling page switching between functional modules such as:
 * <ul>
 *     <li>Home</li>
 *     <li>Doctors management</li>
 *     <li>Patients management</li>
 *     <li>Appointments management</li>
 * </ul>
 * </p>
 *
 * <p>
 * It dynamically loads FXML views into a central content area without requiring
 * full scene replacements, enabling a smooth single‑window application experience.
 * </p>
 */

public class MainController {

    // Root container for dynamically loaded pages
    @FXML
    private StackPane contentArea;

    /**
     * Initializes the controller after FXML loading.
     */
    @FXML
    public void initialize() {
        loadPage("home");
    }

    // Navigation handlers
    @FXML
    private void goHome() {
        loadPage("home");
    }

    @FXML
    private void goDoctors() {
        loadPage("doctor");
    }

    @FXML
    private void goPatients() {
        loadPage("patient");
    }

    @FXML
    private void goAppointments() {
        loadPage("appointment");
    }

    /**
     * Loads a specific page into the main content area.
     *
     * <p>
     * This method clears the current view and dynamically loads a new FXML
     * layout using {@link App#loadNode(String)}.
     * </p>
     *
     * @param page the name of the FXML page to load (without extension)
     */
    private void loadPage(String page) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(App.loadNode(page));
    }
}