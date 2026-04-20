package org.example.clinic.models;

import org.example.clinic.models.enums.AppointmentStatus;

import java.time.LocalDateTime;

/**
 * Represents an appointment between a patient and a doctor in the application.
 *
 * <p>
 * This class models the core scheduling entity of the system, linking a {@link Patient}
 * and a {@link Doctor} at a specific date and time, along with the purpose and status
 * of the appointment.
 * </p>
 */
public class Appointment {

    private final int id;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private String reason;
    private AppointmentStatus status;

    /**
     * Constructs a fully initialized {@code Appointment} instance.
     */
    public Appointment(int id, Patient patient, Doctor doctor,
                       LocalDateTime dateTime,
                       String reason,
                       AppointmentStatus status) {

        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getReason() {
        return reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    // Setters
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Returns a representation of the appointment.
     */
    @Override
    public String toString() {
        return "Appointment #" + id + " - " +
                (patient != null ? patient.getFullName() : "No Patient") +
                " with " +
                (doctor != null ? doctor.getFullName() : "No Doctor");
    }
}