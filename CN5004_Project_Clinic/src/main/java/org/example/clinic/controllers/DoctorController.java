package org.example.clinic.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.clinic.common.ValidationUtils;
import org.example.clinic.models.Doctor;
import org.example.clinic.models.enums.DoctorSpeciality;
import org.example.clinic.services.DoctorService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing doctor‑related UI operations.
 *
 * <p>
 * It handles all CRUD functionality for doctors, including:
 * <ul>
 *     <li>Adding new doctors</li>
 *     <li>Updating existing doctors</li>
 *     <li>Deleting doctors</li>
 *     <li>Searching and filtering doctor records</li>
 * </ul>
 * </p>
 *
 * <p>
 * It also binds JavaFX UI components (TableView, forms, and filters) to the
 * underlying {@link DoctorService} business logic layer.
 * </p>
 */

public class DoctorController extends BaseController {

    private final DoctorService service = new DoctorService();

    @FXML
    private TextField firstNameField, lastNameField, phoneField, emailField, searchField;
    @FXML
    private ComboBox<DoctorSpeciality> specialityBox;
    @FXML
    private ComboBox<String> workingHoursBox;
    @FXML
    private TableView<Doctor> doctorTable;
    @FXML
    private TableColumn<Doctor, Integer> idColumn;
    @FXML
    private TableColumn<Doctor, String> nameColumn, specialityColumn, emailColumn, phoneColumn, hoursColumn;
    @FXML
    private Button updateBtn, deleteBtn;
    @FXML
    private VBox root;

    /**
     * Initializes the controller after FXML loading.
     *
     * <p> Sets up UI components, table bindings, event listeners, validation rules,
     * and default application state. </p>
     */
    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        specialityColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getSpeciality().toString()));
        emailColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        phoneColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPhone()));
        hoursColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getWorkingHours()));

        searchField.textProperty().addListener((obs, old, text) -> filter(text));

        updateBtn.setVisible(false);
        deleteBtn.setVisible(false);
        applyFade(root);

        specialityBox.getItems().addAll(DoctorSpeciality.values());
        workingHoursBox.getItems().addAll(
                "06:00 - 14:00",
                "08:00 - 16:00",
                "09:00 - 17:00",
                "14:00 - 22:00"
        );

        phoneField.textProperty().addListener((obs, o, n) -> {
            if (!n.matches("\\d*")) phoneField.setText(n.replaceAll("\\D", ""));
        });

        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, old, d) -> {
            if (d != null) {
                firstNameField.setText(d.getFirstName());
                lastNameField.setText(d.getLastName());
                phoneField.setText(d.getPhone());
                emailField.setText(d.getEmail());
                specialityBox.setValue(d.getSpeciality());
                workingHoursBox.setValue(d.getWorkingHours());

                updateBtn.setVisible(true);
                deleteBtn.setVisible(true);
            } else {
                updateBtn.setVisible(false);
                deleteBtn.setVisible(false);
            }
        });

        refresh();
    }

    /**
     * Validates doctor input fields before creation or update.
     *
     * @throws IllegalArgumentException if any field is invalid
     */
    private void validateDoctor() {
        ValidationUtils.validateName(firstNameField.getText(), "First name");
        ValidationUtils.validateName(lastNameField.getText(), "Last name");
        ValidationUtils.validatePhone(phoneField.getText());
        ValidationUtils.validateEmail(emailField.getText());
        ValidationUtils.validateRequired(specialityBox.getValue(), "Specialty");
        ValidationUtils.validateRequired(workingHoursBox.getValue(), "Working hours");
    }

    /**
     * Creates a new doctor record.
     */
    @FXML
    private void addDoctor() {
        try {
            validateDoctor();

            service.createDoctor(
                    firstNameField.getText(),
                    lastNameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    specialityBox.getValue(),
                    workingHoursBox.getValue()
            );

            refresh();
            clear();

        } catch (Exception e) {
            show(e.getMessage());
        }
    }

    /**
     * Updates the selected doctor
     */
    @FXML
    private void updateDoctor() {
        Doctor d = doctorTable.getSelectionModel().getSelectedItem();

        if (d == null) {
            show("Select a doctor to edit");
            return;
        }

        try {
            validateDoctor();

            d.setFirstName(firstNameField.getText());
            d.setLastName(lastNameField.getText());
            d.setPhone(phoneField.getText());
            d.setEmail(emailField.getText());
            d.setSpeciality(specialityBox.getValue());
            d.setWorkingHours(workingHoursBox.getValue());

            service.updateDoctor();
            refresh();
            clear();

        } catch (Exception e) {
            show(e.getMessage());
        }
    }

    /**
     * Deletes the selected doctor from the system.
     */
    @FXML
    private void deleteDoctor() {
        Doctor d = doctorTable.getSelectionModel().getSelectedItem();

        if (d == null) {
            show("Select a doctor to delete");
            return;
        }

        if (!confirm("Are you sure you want to delete this doctor?")) return;

        service.deleteDoctor(d);
        refresh();
        clear();
    }

    /**
     * Filters doctor table based on search input.
     *
     * @param text search query
     */
    private void filter(String text) {
        List<Doctor> filtered = service.getAllDoctors().stream()
                .filter(d ->
                        d.getFullName().toLowerCase().contains(text.toLowerCase()) ||
                                d.getEmail().toLowerCase().contains(text.toLowerCase()) ||
                                d.getPhone().contains(text) ||
                                d.getSpeciality().name().toLowerCase().contains(text.toLowerCase())
                )
                .collect(Collectors.toList());

        doctorTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Reloads doctor data from service and refreshes the table view.
     */
    private void refresh() {
        service.reload();
        doctorTable.setItems(FXCollections.observableArrayList(service.getAllDoctors()));
    }

    /**
     * Clears all input fields and resets UI state.
     */
    private void clear() {
        firstNameField.clear();
        lastNameField.clear();
        phoneField.clear();
        emailField.clear();
        specialityBox.setValue(null);
        workingHoursBox.setValue(null);
        doctorTable.getSelectionModel().clearSelection();
        updateBtn.setVisible(false);
        deleteBtn.setVisible(false);
    }
}