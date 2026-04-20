package org.example.clinic.models;

import org.example.clinic.common.Utils;

import java.time.LocalDate;

/**
 * Represents a patient within the application.
 *
 * <p> It encapsulates all relevant information related to a patient. </p>
 *
 * <p> Name fields are automatically capitalised using
 * {@link Utils#capitalise(String)} to ensure consistent formatting. </p>
 */
public class Patient {

    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String taxId;
    private LocalDate dob;
    private String gender;
    private String medicalHistory;

    /**
     * Default constructor.
     * <p> Required for frameworks, serialization, and data mapping processes. </p>
     */
    public Patient() {
    }

    /**
     * Constructs a fully initialized {@code Patient} instance.
     */
    public Patient(int id, String firstName, String lastName,
                   String phone, String email, String taxId,
                   LocalDate dob, String gender, String medicalHistory) {

        this.id = id;
        this.firstName = Utils.capitalise(firstName);
        this.lastName = Utils.capitalise(lastName);
        this.phone = phone;
        this.email = email;
        this.taxId = taxId;
        this.dob = dob;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getTaxId() {
        return taxId;
    }

    public LocalDate getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = Utils.capitalise(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName = Utils.capitalise(lastName);
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    /**
     * Returns the full name of the patient.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns a string representation of the patient.
     */
    @Override
    public String toString() {
        return getFullName() + " - " + phone;
    }
}