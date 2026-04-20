package org.example.clinic.models;

import org.example.clinic.common.Utils;
import org.example.clinic.models.enums.DoctorSpeciality;

/**
 * Represents a doctor within the application.
 *
 * <p> It encapsulates all relevant information related to a doctor. </p>
 *
 * <p> Name fields are automatically capitalised using
 * {@link Utils#capitalise(String)} to ensure consistent formatting. </p>
 */

public class Doctor {

    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private DoctorSpeciality speciality;
    private String workingHours;

    /**
     * Default constructor.
     * <p> Required for frameworks and serialization/deserialization processes.</p>
     */
    public Doctor() {
    }

    /**
     * Constructs a fully initialized {@code Doctor} instance.
     */
    public Doctor(int id, String firstName, String lastName,
                  String phone, String email,
                  DoctorSpeciality specialty,
                  String workingHours) {

        this.id = id;
        this.firstName = Utils.capitalise(firstName);
        this.lastName = Utils.capitalise(lastName);
        this.phone = phone;
        this.email = email;
        this.speciality = specialty;
        this.workingHours = workingHours;
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

    public DoctorSpeciality getSpeciality() {
        return speciality;
    }

    public String getWorkingHours() {
        return workingHours;
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

    public void setSpeciality(DoctorSpeciality specialty) {
        this.speciality = specialty;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    /**
     * Returns the full name of the doctor.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Returns a string representation of the doctor.
     */
    @Override
    public String toString() {
        return getFullName() + " (" + speciality + ")";
    }
}