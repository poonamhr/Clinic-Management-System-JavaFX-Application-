package org.example.clinic.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.example.clinic.services.*;

import java.io.File;

/**
 * Controller responsible for managing the Home dashboard view.
 *
 * <p>
 * It handles:
 * <ul>
 *     <li>Dashboard initialization and UI animations</li>
 *     <li>Displaying the main landing page content</li>
 *     <li>Exporting system data (Doctors, Patients, Appointments)</li>
 * </ul>
 * </p>
 *
 * <p>
 * It serves as the entry dashboard for administrative operations,
 * providing quick access to reporting and data export features.
 * </p>
 */

public class HomeController extends BaseController {

    // Services responsible for doctor/patient/appointment-related operations.
    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService();
    private final AppointmentService appointmentService =
            new AppointmentService(doctorService, patientService);


    @FXML
    private ImageView imageView;
    @FXML
    private BorderPane root;
    @FXML
    private Label titleText;
    @FXML
    private Label subtitleText;
    @FXML
    private HBox cardContainer;

    /**
     * Initializes the Home view after FXML loading.
     * <p>
     * Loads the image and applies fade-in animations to key UI components.
     */
    @FXML
    public void initialize() {
        // Load image
        var url = getClass().getResource("/org/example/clinic/images/index.png");
        if (url != null) {
            imageView.setImage(new Image(url.toExternalForm()));
        }

        applyFade(titleText);
        applyFade(subtitleText);
        applyFade(cardContainer);
    }

    /**
     * Opens a file chooser dialog for exporting CSV files.
     *
     * @param name base name of the file to be saved
     * @return selected file, or {@code null} if the user cancels
     */
    private File chooseFile(String name) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save " + name);
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fc.setInitialFileName(name + ".csv");
        return fc.showSaveDialog(root.getScene().getWindow());
    }

    /**
     * Exports all doctors/patients to a CSV file selected by the user.
     */
    @FXML
    private void exportDoctors() {
        File file = chooseFile("doctors_export");
        if (file == null) return;

        ExportService.exportDoctorsToFile(doctorService.getAllDoctors(), file);
        show("Doctors exported successfully!");
    }

    @FXML
    private void exportPatients() {
        File file = chooseFile("patients_export");
        if (file == null) return;

        ExportService.exportPatientsToFile(patientService.getAllPatients(), file);
        show("Patients exported successfully!");
    }

    /**
     * Exports today's appointments to a CSV file selected by the user.
     */
    @FXML
    private void exportAppointments() {
        File file = chooseFile("appointments_export");
        if (file == null) return;

        ExportService.exportTodayAppointmentsToFile(
                appointmentService.getAllAppointments(), file
        );

        show("Today's appointments exported successfully!");
    }
}