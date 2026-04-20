package org.example.clinic.services;

import org.example.clinic.common.AppConstants;
import org.example.clinic.common.ValidationUtils;
import org.example.clinic.models.Patient;
import org.example.clinic.persistence.FileManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for managing {@link Patient} entities.
 *
 * <p>
 * It implements the business logic related to patients,
 * providing functionality for creating, data loading, updating, persistence, deleting, and validating
 * patient records.
 * </p>
 *
 * <p>
 * It acts as an intermediary between the presentation layer (UI/controllers)
 * and the persistence layer (file storage), ensuring that all business rules
 * are enforced before data is saved.
 * </p>
 *
 * <p>
 * Responsibilities includes:
 * <ul>
 *     <li>Loading patient data from persistence storage (CSV)</li>
 *     <li>Validating patient information before persistence</li>
 *     <li>Enforcing uniqueness constraints (email, phone, tax ID)</li>
 *     <li>Managing in-memory patient collection</li>
 *     <li>Persisting changes back to file storage</li>
 * </ul>
 * </p>
 */
public class PatientService {

    // In-memory list of patients
    private final List<Patient> patients;

    /**
     * Initializes service and loads data.
     */
    public PatientService() {
        patients = new ArrayList<>();
        loadFromFile();
    }

    /**
     * Loads patient records from CSV file into memory.
     * <p>
     * Existing in-memory data is cleared before loading.
     * Invalid or corrupted records are skipped to ensure system stability.
     */
    private void loadFromFile() {
        patients.clear();
        List<String> lines = FileManager.loadLines(AppConstants.PATIENTS_FILE);

        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("ID,")) continue;

            // Skip empty lines and header
            String[] p = line.split(",", -1);

            // Skip invalid rows
            if (p.length < 9) {
                System.out.println("Skipping invalid patient line (not enough columns): " + line);
                continue;
            }

            try {
                int id = Integer.parseInt(p[0].trim());
                String firstName = p[1].trim();
                String lastName = p[2].trim();
                String phone = p[3].trim();
                String email = p[4].trim();
                String taxId = p[5].trim();
                LocalDate dob = LocalDate.parse(p[6].trim());
                String gender = p[7].trim();
                String history = p[8].trim();

                Patient patient = new Patient(
                        id,
                        firstName,
                        lastName,
                        phone,
                        email,
                        taxId,
                        dob,
                        gender,
                        history
                );

                patients.add(patient);

            } catch (Exception e) {
                System.out.println("Skipping invalid patient line: " + line);
            }
        }
    }

    /**
     * Returns all patients
     */
    public List<Patient> getAllPatients() {
        return patients;
    }

    /**
     * Generates next patient ID.
     * It is calculated based on the current maximum ID + 1 in the system.
     */
    public int generateId() {
        return patients.stream().mapToInt(Patient::getId).max().orElse(0) + 1;
    }

    /**
     * Creates and saves a new patient after validation.
     *
     * @throws IllegalArgumentException if validation fails or duplicates are found
     */
    public Patient createPatient(String firstName, String lastName,
                                 String phone, String email,
                                 String taxId, LocalDate dob,
                                 String gender, String medicalHistory) {

        // Validation
        ValidationUtils.validateName(firstName, "First name");
        ValidationUtils.validateName(lastName, "Last name");
        ValidationUtils.validatePhone(phone);
        ValidationUtils.validateEmail(email);
        ValidationUtils.validateTaxId(taxId);
        ValidationUtils.validateRequired(dob, "Date of birth");
        ValidationUtils.validateRequired(gender, "Gender");
        ValidationUtils.validateRequired(medicalHistory, "Medical history");

        // Uniqueness checks
        if (patients.stream().anyMatch(p -> p.getEmail().equalsIgnoreCase(email)))
            throw new IllegalArgumentException("Email already exists!");

        if (patients.stream().anyMatch(p -> p.getPhone().equals(phone)))
            throw new IllegalArgumentException("Phone already exists!");

        if (patients.stream().anyMatch(p -> p.getTaxId().equals(taxId)))
            throw new IllegalArgumentException("Tax ID already exists!");

        Patient p = new Patient(
                generateId(),
                firstName,
                lastName,
                phone,
                email,
                taxId,
                dob,
                gender,
                medicalHistory
        );

        patients.add(p);
        save();
        return p;
    }

    /**
     * Saves current data (used after updates).
     */
    public void updatePatient() {
        save();
    }

    /**
     * Deletes a patient and saves changes.
     */
    public void deletePatient(Patient patient) {
        patients.remove(patient);
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
     * Writes patients to CSV file.
     */
    private void save() {
        List<String> lines = new ArrayList<>();

        // Header
        lines.add("ID,FirstName,LastName,Phone,Email,TaxID,DOB,Gender,History");

        for (Patient p : patients) {
            lines.add(
                    p.getId() + "," +
                            p.getFirstName() + "," +
                            p.getLastName() + "," +
                            p.getPhone() + "," +
                            p.getEmail() + "," +
                            p.getTaxId() + "," +
                            p.getDob() + "," +
                            p.getGender() + "," +
                            p.getMedicalHistory()
            );
        }

        FileManager.saveLines(AppConstants.PATIENTS_FILE, lines);
    }
}