package org.example.clinic.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.example.clinic.models.Appointment;
import org.example.clinic.models.Doctor;
import org.example.clinic.models.Patient;
import org.example.clinic.models.enums.AppointmentStatus;
import org.example.clinic.services.AppointmentService;
import org.example.clinic.services.DoctorService;
import org.example.clinic.services.PatientService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing appointment‑related UI operations.
 *
 * <p>
 * It handles all CRUD functionality for appointments, including:
 * <ul>
 *     <li>Creating new appointments</li>
 *     <li>Updating existing appointments</li>
 *     <li>Canceling appointments</li>
 *     <li>Searching and filtering scheduled appointments</li>
 * </ul>
 * </p>
 *
 * <p>
 * It coordinates between the JavaFX UI layer and the service layer
 * ({@link AppointmentService}, {@link DoctorService}, {@link PatientService}),
 * ensuring that business rules such as scheduling conflicts, working hours,
 * and time validation are enforced.
 * </p>
 */

public class AppointmentController extends BaseController {

    private final DoctorService doctorService = new DoctorService();
    private final PatientService patientService = new PatientService();
    private final AppointmentService service = new AppointmentService(doctorService, patientService);

    // Prevents triggering events during programmatic doctor selection
    private boolean isUserSelectingDoctor = true;

    @FXML private ComboBox<Doctor> doctorBox;
    @FXML private ComboBox<Patient> patientBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeBox;
    @FXML private TextArea reasonField;
    @FXML private TextField searchField;
    @FXML private TableView<Appointment> appointmentTable;
    @FXML private TableColumn<Appointment, Integer> idColumn;
    @FXML private TableColumn<Appointment,String> patientColumn, doctorColumn, dateColumn, statusColumn, reasonColumn;
    @FXML private Button updateBtn, cancelBtn;
    @FXML private VBox root;

    /**
     * Initializes the controller after FXML loading.
     *
     * <p> Sets up UI components, loads doctors and patients into selection boxes,
     * configures table columns, applies validation rules, and prepares
     * appointment scheduling constraints such as working hours and date limits. </p>
     */
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
        patientColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPatient().getFullName()));
        doctorColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDoctor().getFullName()));
        dateColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateTime().toString()));
        statusColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().toString()));
        reasonColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getReason()));

        searchField.textProperty().addListener((obs, old, text) -> filter(text));

        updateBtn.setVisible(false);
        cancelBtn.setVisible(false);
        applyFade(root);

        doctorBox.setItems(FXCollections.observableArrayList(doctorService.getAllDoctors()));
        patientBox.setItems(FXCollections.observableArrayList(patientService.getAllPatients()));
        timeBox.setDisable(true);

        // Disable past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        // When doctor changes, load their working hours into timeBox
        doctorBox.valueProperty().addListener((obs, old, doctor) -> {
            if (!isUserSelectingDoctor) return;

            if (doctor != null) {
                timeBox.setDisable(false);
                loadDoctorHours(doctor.getWorkingHours());
                show("Doctor available: " + doctor.getWorkingHours());
            } else {
                timeBox.setDisable(true);
                timeBox.getItems().clear();
                timeBox.setValue(null);
            }

        });

        appointmentTable.getSelectionModel().selectedItemProperty().addListener((obs, old, a) -> {
            if (a != null) {

                isUserSelectingDoctor = false;
                doctorBox.setValue(a.getDoctor());
                isUserSelectingDoctor = true;

                // ensure timeBox is filled for this doctor's hours
                loadDoctorHours(a.getDoctor().getWorkingHours());

                patientBox.setValue(a.getPatient());
                datePicker.setValue(a.getDateTime().toLocalDate());
                timeBox.setValue(a.getDateTime().toLocalTime().toString());
                reasonField.setText(a.getReason());

                updateBtn.setVisible(true);
                cancelBtn.setVisible(true);
            }
        });

        refresh();
    }

    /**
     * Builds a valid {@link LocalDateTime} from UI inputs.
     *
     * <p> Validates doctor, patient, date, time, and reason fields
     * and ensures no past-time appointments are scheduled. </p>
     *
     * @return constructed appointment date-time
     * @throws IllegalArgumentException if any required field is invalid
     */
    private LocalDateTime buildDateTime() {
        if (doctorBox.getValue() == null)
            throw new IllegalArgumentException("Select a doctor");

        if (patientBox.getValue() == null)
            throw new IllegalArgumentException("Select a patient");

        if (datePicker.getValue() == null)
            throw new IllegalArgumentException("Select a date");

        if (timeBox.getValue() == null)
            throw new IllegalArgumentException("Select a time");

        if (reasonField.getText().trim().isEmpty())
            throw new IllegalArgumentException("Reason is required");

        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeBox.getValue());

        if (date.isEqual(LocalDate.now()) && time.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("Cannot select past time today");
        }

        return LocalDateTime.of(date, time);
    }

    /**
     * Loads available appointment time slots based on a doctor's working hours.
     *
     * @param hours working hours in format "HH:mm - HH:mm"
     */
    private void loadDoctorHours(String hours) {
        timeBox.getItems().clear();

        try {
            // Parse start and end times
            String[] parts = hours.split(" - ");
            LocalTime start = LocalTime.parse(parts[0].trim());
            LocalTime end = LocalTime.parse(parts[1].trim());

            // Generate hourly time slots
            LocalTime t = start;
            while (!t.isAfter(end.minusHours(1))) {
                timeBox.getItems().add(t.toString());
                t = t.plusHours(1);
            }

        } catch (Exception e) {
            show("Doctor working hours are invalid.");
        }
    }

    /**
     * Creates a new appointment if no scheduling conflicts exist.
     */
    @FXML
    private void addAppointment() {
        try {
            LocalDateTime dt = buildDateTime();
            Appointment a = service.createAppointment(
                    doctorBox.getValue(),
                    patientBox.getValue(),
                    dt,
                    reasonField.getText().trim()
            );
            // conflict handled by service returning null
            if (a == null) {
                show("This appointment slot is already booked (doctor or patient).");
                return;
            }
            refresh();
            clear();
        } catch (IllegalArgumentException ex) {
            show(ex.getMessage());
        } catch (Exception e) {
            show("Invalid data");
        }
    }

    /**
     * Updates the selected appointment, checking for conflicts and
     * updating status if rescheduled.
     */
    @FXML
    private void updateAppointment() {
        Appointment a = appointmentTable.getSelectionModel().getSelectedItem();
        if (a == null) {
            show("Select an appointment to update");
            return;
        }

        try {
            LocalDateTime newDT = buildDateTime();

            if (!service.updateAppointment(a, doctorBox.getValue(), patientBox.getValue(), newDT)) {
                show("This appointment slot is already booked (doctor or patient).");
                return;
            }

            LocalDateTime oldDT = a.getDateTime();

            a.setDoctor(doctorBox.getValue());
            a.setPatient(patientBox.getValue());
            a.setDateTime(newDT);
            a.setReason(reasonField.getText().trim());

            if (!oldDT.equals(newDT)) {
                a.setStatus(AppointmentStatus.RESCHEDULED);
            } else {
                a.setStatus(AppointmentStatus.SCHEDULED);
            }

            service.updateAppointments();

            refresh();
            appointmentTable.refresh();
            clear();

        } catch (IllegalArgumentException ex) {
            show(ex.getMessage());
        } catch (Exception e) {
            show("Invalid data");
        }
    }

    /**
     * Cancels the selected appointment and updates its status.
     */
    @FXML
    private void cancelAppointment() {
        Appointment a = appointmentTable.getSelectionModel().getSelectedItem();
        if (a == null) {
            show("Select an appointment to cancel");
            return;
        }

        if (!confirm("Are you sure you want to cancel this appointment?")) return;

        a.setStatus(AppointmentStatus.CANCELLED);

        service.updateAppointments();
        refresh();
        appointmentTable.refresh();
        clear();
    }

    /**
     * Filters appointment table based on search input.
     *
     * @param text search keyword
     */
    private void filter(String text) {
        String lower = text.toLowerCase();

        List<Appointment> filtered = service.getAllAppointments().stream()
                .filter(a ->
                        a.getPatient().getFullName().toLowerCase().contains(lower) ||
                                a.getDoctor().getFullName().toLowerCase().contains(lower) ||
                                a.getDateTime().toString().toLowerCase().contains(lower) ||
                                a.getStatus().name().toLowerCase().contains(lower)
                )
                .collect(Collectors.toList());

        appointmentTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Reloads appointment data from service and refreshes the table view.
     */
    private void refresh() {
        appointmentTable.setItems(FXCollections.observableArrayList(service.getAllAppointments()));
    }

    /**
     * Clears all input fields and resets UI state.
     */
    private void clear() {
        isUserSelectingDoctor = false;
        doctorBox.setValue(null);
        patientBox.setValue(null);
        isUserSelectingDoctor = true;
        datePicker.setValue(null);
        timeBox.getItems().clear();
        timeBox.setValue(null);
        reasonField.clear();
        appointmentTable.getSelectionModel().clearSelection();
        updateBtn.setVisible(false);
        cancelBtn.setVisible(false);
    }
}
