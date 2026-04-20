package org.example.clinic.services;

import org.example.clinic.common.AppConstants;
import org.example.clinic.common.ValidationUtils;
import org.example.clinic.models.Doctor;
import org.example.clinic.models.enums.DoctorSpeciality;
import org.example.clinic.persistence.FileManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing {@link Doctor} entities.
 *
 * <p>
 * It implements the business logic related to doctors,
 * providing functionality for creating, data loading, updating, persistence, deleting, and validating
 * doctor records.
 * </p>
 *
 * <p>
 * It acts as an intermediary between the presentation layer (UI/controllers)
 * and the persistence layer (file storage), ensuring that all business rules
 * are enforced before data is saved.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Loading doctor data from persistent storage (CSV)</li>
 *     <li>Validating doctor information before persistence</li>
 *     <li>Ensuring uniqueness constraints (email, phone)</li>
 *     <li>Managing in-memory doctor collection</li>
 *     <li>Persisting changes back to file storage</li>
 * </ul>
 * </p>
 */
public class DoctorService {

    // In-memory list of doctors
    private final List<Doctor> doctors;

    /**
     * Initializes service and loads data.
     */
    public DoctorService() {
        doctors = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Loads doctor records from CSV file into memory.
     * <p>
     * Existing in-memory data is cleared before loading.
     * Invalid or corrupted records are skipped to ensure system stability.
     */
    private void loadFromFile() {
        doctors.clear();
        List<String> lines = FileManager.loadLines(AppConstants.DOCTORS_FILE);

        for (String line : lines) {

            // Skip empty lines and header
            if (line.trim().isEmpty() || line.startsWith("ID,")) continue;
            String[] p = line.split(",", -1);

            // Skip invalid rows
            if (p.length < 7) {
                System.out.println("Skipping invalid doctor line: " + line);
                continue;
            }

            try {
                DoctorSpeciality specialty = DoctorSpeciality.valueOf(p[5].trim());

                Doctor d = new Doctor(
                        Integer.parseInt(p[0]),
                        p[1],
                        p[2],
                        p[3],
                        p[4],
                        specialty,
                        p[6]
                );

                doctors.add(d);

            } catch (Exception e) {
                System.out.println("Skipping invalid doctor line: " + line);
            }
        }
    }

    /**
     * Returns all doctors
     */
    public List<Doctor> getAllDoctors() {
        return doctors;
    }

    /**
     * Generates next doctor ID.
     * It is calculated based on the current maximum ID + 1 in the system.
     */
    public int generateId() {
        return doctors.stream().mapToInt(Doctor::getId).max().orElse(0) + 1;
    }

    /**
     * Creates and saves a new doctor after validation.
     *
     * @throws IllegalArgumentException if validation fails or duplicates are found
     */
    public Doctor createDoctor(String firstName, String lastName,
                               String phone, String email,
                               DoctorSpeciality speciality,
                               String workingHours) {

        // Validation
        ValidationUtils.validateName(firstName, "First name");
        ValidationUtils.validateName(lastName, "Last name");
        ValidationUtils.validatePhone(phone);
        ValidationUtils.validateEmail(email);
        ValidationUtils.validateRequired(speciality, "Speciality");
        ValidationUtils.validateRequired(workingHours, "Working hours");

        // Uniqueness checks
        if (doctors.stream().anyMatch(d -> d.getEmail().equalsIgnoreCase(email)))
            throw new IllegalArgumentException("Email already exists!");

        if (doctors.stream().anyMatch(d -> d.getPhone().equals(phone)))
            throw new IllegalArgumentException("Phone already exists!");

        Doctor doctor = new Doctor(
                generateId(),
                firstName,
                lastName,
                phone,
                email,
                speciality,
                workingHours
        );

        doctors.add(doctor);
        save();
        return doctor;
    }


    /**
     * Saves current data (used after updates).
     */
    public void updateDoctor() {
        save();
    }

    /**
     * Deletes a doctor and saves changes.
     */
    public void deleteDoctor(Doctor doctor) {
        doctors.remove(doctor);
        save();
    }

    /**
     * Reloads data from file.
     * It refreshes the dataset from the CSV file.
     */
    public void reload() {
        loadFromFile();
    }

    /**
     * Writes doctors to CSV file.
     */
    private void save() {
        List<String> lines = new ArrayList<>();

        // Header
        lines.add("ID,FirstName,LastName,Phone,Email,Specialty,WorkingHours");

        for (Doctor d : doctors) {
            lines.add(
                    d.getId() + "," +
                            d.getFirstName() + "," +
                            d.getLastName() + "," +
                            d.getPhone() + "," +
                            d.getEmail() + "," +
                            d.getSpeciality() + "," +
                            d.getWorkingHours()
            );
        }

        FileManager.saveLines(AppConstants.DOCTORS_FILE, lines);
    }
}
