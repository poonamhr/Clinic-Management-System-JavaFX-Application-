package org.example.clinic.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.clinic.common.ValidationUtils;
import org.example.clinic.models.Patient;
import org.example.clinic.services.PatientService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing patient‑related UI operations.
 *
 * <p>
 * It handles all CRUD functionality for patients, including:
 * <ul>
 *     <li>Adding new patients</li>
 *     <li>Updating existing patients</li>
 *     <li>Deleting patients</li>
 *     <li>Searching and filtering patient records</li>
 * </ul>
 * </p>
 *
 * <p>
 * It binds JavaFX UI components (forms, tables, filters) to the
 * underlying {@link PatientService} business logic layer.
 * </p>
 */

public class PatientController extends BaseController {

    private final PatientService service = new PatientService();

    @FXML
    private TextField firstNameField, lastNameField, phoneField, emailField, taxIdField, searchField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private ComboBox<String> genderBox;
    @FXML
    private TextArea historyArea;
    @FXML
    private TableView<Patient> patientTable;
    @FXML
    private TableColumn<Patient, Integer> idColumn;
    @FXML
    private TableColumn<Patient, String> nameColumn, emailColumn, phoneColumn, taxIdColumn, genderColumn, historyColumn, dobColumn;
    @FXML
    private Button updateBtn, deleteBtn;
    @FXML
    private VBox root;

    /**
     * Initializes the controller after FXML loading.
     *
     * <p> Configures UI components, table bindings, validation rules,
     * and default application state. </p>
     */
    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        emailColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        phoneColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        taxIdColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTaxId()));
        genderColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getGender()));
        historyColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMedicalHistory()));
        dobColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDob().toString()));

        searchField.textProperty().addListener((obs, old, text) -> filter(text));

        updateBtn.setVisible(false);
        deleteBtn.setVisible(false);
        applyFade(root);

        genderBox.getItems().addAll("Male", "Female", "Other");

        // Prevent future dates
        dobPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                firstNameField.setText(p.getFirstName());
                lastNameField.setText(p.getLastName());
                phoneField.setText(p.getPhone());
                emailField.setText(p.getEmail());
                taxIdField.setText(p.getTaxId());
                dobPicker.setValue(p.getDob());
                genderBox.setValue(p.getGender());
                historyArea.setText(p.getMedicalHistory());

                updateBtn.setVisible(true);
                deleteBtn.setVisible(true);
            }
        });

        refresh();
    }

    /**
     * Validates patient input fields before creation or update.
     *
     * @throws IllegalArgumentException if any field is invalid
     */
    private void validatePatient() {

        ValidationUtils.validateName(firstNameField.getText(), "First name");
        ValidationUtils.validateName(lastNameField.getText(), "Last name");
        ValidationUtils.validatePhone(phoneField.getText());
        ValidationUtils.validateEmail(emailField.getText());
        ValidationUtils.validateTaxId(taxIdField.getText());
        ValidationUtils.validateRequired(dobPicker.getValue(), "Date of birth");
        ValidationUtils.validateRequired(genderBox.getValue(), "Gender");

        if (historyArea.getText().trim().isEmpty())
            throw new IllegalArgumentException("Medical history is required");
    }

    /**
     * Creates a new patient record.
     */
    @FXML
    private void addPatient() {
        try {
            validatePatient();

            service.createPatient(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    taxIdField.getText(),
                    dobPicker.getValue(),
                    genderBox.getValue(),
                    historyArea.getText()
            );

            refresh();
            clear();

        } catch (Exception e) {
            show(e.getMessage());
        }
    }

    /**
     * Updates the selected patient
     */
    @FXML
    private void updatePatient() {
        Patient p = patientTable.getSelectionModel().getSelectedItem();

        if (p == null) {
            show("Select a patient to update");
            return;
        }

        try {
            validatePatient();

            p.setFirstName(firstNameField.getText());
            p.setLastName(lastNameField.getText());
            p.setPhone(phoneField.getText());
            p.setEmail(emailField.getText());
            p.setTaxId(taxIdField.getText());
            p.setGender(genderBox.getValue());
            p.setMedicalHistory(historyArea.getText());
            p.setDob(dobPicker.getValue());

            service.updatePatient();
            refresh();
            clear();

        } catch (Exception e) {
            show(e.getMessage());
        }
    }

    /**
     * Deletes the selected patient from the system.
     */
    @FXML
    private void deletePatient() {
        Patient p = patientTable.getSelectionModel().getSelectedItem();

        if (p == null) {
            show("Select a patient to delete");
            return;
        }

        if (!confirm("Are you sure you want to delete this patient?")) return;

        service.deletePatient(p);
        refresh();
        clear();
    }

    /**
     * Filters patient table based on search input.
     *
     * @param text search query
     */
    private void filter(String text) {
        List<Patient> filtered = service.getAllPatients().stream()
                .filter(p ->
                        p.getFullName().toLowerCase().contains(text.toLowerCase()) ||
                                p.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                                p.getPhone().contains(text) ||
                                p.getTaxId().toLowerCase().contains(text) ||
                                p.getGender().toLowerCase().contains(text)
                )
                .collect(Collectors.toList());

        patientTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Reloads patient data from service and refreshes the table view.
     */
    private void refresh() {
        service.reload();
        patientTable.setItems(FXCollections.observableArrayList(service.getAllPatients()));
    }

    /**
     * Clears all input fields and resets UI state.
     */
    private void clear() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        taxIdField.clear();
        dobPicker.setValue(null);
        genderBox.setValue(null);
        historyArea.clear();
        patientTable.getSelectionModel().clearSelection();
        updateBtn.setVisible(false);
        deleteBtn.setVisible(false);
    }
}
