package org.example.clinic.services;

import org.example.clinic.common.AppConstants;
import org.example.clinic.models.Appointment;
import org.example.clinic.models.Doctor;
import org.example.clinic.models.Patient;
import org.example.clinic.models.enums.AppointmentStatus;
import org.example.clinic.persistence.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing {@link Appointment} entities.
 *
 * <p>
 * This class implements the core business logic for appointment handling,
 * including creation, conflict detection, updating, persistence, and
 * loading data from storage.
 * </p>
 *
 * <p>
 * It also collaborates with {@link DoctorService} and {@link PatientService}
 * to resolve entity relationships during appointment reconstruction.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Loading appointments from persistent storage (CSV)</li>
 *     <li>Reconstructing doctor and patient references</li>
 *     <li>Preventing scheduling conflicts</li>
 *     <li>Managing appointment lifecycle and status</li>
 *     <li>Persisting changes back to file storage</li>
 * </ul>
 * </p>
 */

public class AppointmentService {

    // In-memory list of appointments
    private final List<Appointment> appointments;

    private final DoctorService doctorService;
    private final PatientService patientService;

    /**
     * Initializes services and loads data.
     */
    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        appointments = new ArrayList<>();
        loadFromFile();
    }


    /**
     * Loads appointment data from CSV file into memory.
     * <p>
     * This method reconstructs full {@link Appointment} objects by resolving
     * referenced doctor and patient IDs using their respective services.
     * Invalid or corrupted records are skipped to ensure system stability.
     */
    private void loadFromFile() {
        appointments.clear();

        List<String> lines = FileManager.loadLines(AppConstants.APPOINTMENTS_FILE);
        boolean cleaned = false;

        boolean first = true;

        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            // Skip header
            if (first && line.startsWith("ID")) {
                first = false;
                continue;
            }
            first = false;

            String[] p = line.split(",", -1);

            // Skip invalid rows
            if (p.length < 6) {
                cleaned = true;
                continue;
            }

            try {
                int id = Integer.parseInt(p[0].trim());
                int patientId = Integer.parseInt(p[1].trim());
                int doctorId = Integer.parseInt(p[2].trim());
                LocalDateTime dateTime = LocalDateTime.parse(p[3].trim());
                String reason = p[4].trim();
                AppointmentStatus status = AppointmentStatus.valueOf(p[5].trim());

                // Resolve patient
                Patient patient = patientService.getAllPatients()
                        .stream()
                        .filter(x -> x.getId() == patientId)
                        .findFirst()
                        .orElse(null);

                // Resolve doctor
                Doctor doctor = doctorService.getAllDoctors()
                        .stream()
                        .filter(x -> x.getId() == doctorId)
                        .findFirst()
                        .orElse(null);

                // Skip invalid references (auto-clean)
                if (patient == null || doctor == null) {
                    cleaned = true;
                    continue;
                }

                appointments.add(
                        new Appointment(id, patient, doctor, dateTime, reason, status)
                );

            } catch (Exception e) {
                cleaned = true;
            }
        }

        // Save cleaned data back to file
        if (cleaned) {
            updateAppointments();
        }
    }


    /**
     * Returns all appointments
     */
    public List<Appointment> getAllAppointments() {
        return appointments;
    }

    /**
     * Generates next appointment ID.
     * It is calculated based on the current maximum ID + 1 in the system.
     */
    private int generateId() {
        return appointments.stream()
                .mapToInt(Appointment::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Creates a new appointment after validating and checking for conflicts.
     * <p>
     * Conflict rules:
     * <ul>
     *     <li>A doctor cannot have multiple appointments at the same time</li>
     *     <li>A patient cannot have multiple appointments at the same time</li>
     *     <li>Cancelled appointments are ignored in conflict checks</li>
     * </ul>
     *
     * <p>
     * If a conflict is detected, this method returns {@code null} instead of throwing
     * an exception to allow the UI layer to handle the response gracefully.
     *
     * @throws IllegalArgumentException if required fields are invalid
     */
    public Appointment createAppointment(Doctor doctor, Patient patient,
                                         LocalDateTime dateTime,
                                         String reason) {

        if (doctor == null || patient == null)
            throw new IllegalArgumentException("Doctor and Patient required");

        if (reason == null || reason.isBlank())
            throw new IllegalArgumentException("Reason required");

        // Check for scheduling conflicts
        boolean conflict = appointments.stream().anyMatch(a -> {

            if (a.getStatus() == AppointmentStatus.CANCELLED) return false;

            return (a.getDoctor().getId() == doctor.getId()
                    && a.getDateTime().equals(dateTime))
                    ||
                    (a.getPatient().getId() == patient.getId()
                            && a.getDateTime().equals(dateTime));
        });

        // Return null to allow controller-level handling
        if (conflict) {
            return null;
        }

        Appointment a = new Appointment(
                generateId(),
                patient,
                doctor,
                dateTime,
                reason,
                AppointmentStatus.SCHEDULED
        );

        appointments.add(a);
        save();

        return a;
    }

    /**
     * Checks if an appointment update is valid (no conflicts). This method does not modify data; it only checks scheduling feasibility.
     *
     * @param current     the appointment being updated
     * @param newDoctor   proposed doctor
     * @param newPatient  proposed patient
     * @param newDateTime proposed date and time
     * @return {@code true} if the update is allowed, otherwise {@code false}
     * @throws IllegalArgumentException if required parameters are null
     */
    public boolean updateAppointment(Appointment current,
                                     Doctor newDoctor,
                                     Patient newPatient,
                                     LocalDateTime newDateTime) {

        if (newDoctor == null || newPatient == null)
            throw new IllegalArgumentException("Doctor/Patient required");

        return appointments.stream().noneMatch(a -> {

            if (a == current) return false;
            if (a.getStatus() == AppointmentStatus.CANCELLED) return false;

            return (a.getDoctor().getId() == newDoctor.getId()
                    && a.getDateTime().equals(newDateTime))
                    ||
                    (a.getPatient().getId() == newPatient.getId()
                            && a.getDateTime().equals(newDateTime));
        });
    }

    /**
     * Saves current data.
     */
    public void updateAppointments() {
        save();
    }


    /**
     * Writes appointments to CSV file.
     */
    private void save() {

        List<String> lines = new ArrayList<>();

        lines.add("ID,Patient,Doctor,DateTime,Reason,Status");

        for (Appointment a : appointments) {
            lines.add(
                    a.getId() + "," +
                            a.getPatient().getId() + "," +
                            a.getDoctor().getId() + "," +
                            a.getDateTime() + "," +
                            a.getReason() + "," +
                            a.getStatus()
            );
        }

        FileManager.saveLines(AppConstants.APPOINTMENTS_FILE, lines);
    }
}