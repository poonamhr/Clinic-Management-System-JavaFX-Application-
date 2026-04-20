package org.example.clinic.services;

import org.example.clinic.models.*;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * Service class responsible for exporting system data to external files.
 *
 * <p>
 * This class provides functionality for exporting key entities such as doctors,
 * patients, and appointments into CSV format for reporting, backup, or external use.
 * </p>
 *
 * <p>
 * Unlike persistence services, this class focuses on <em>data extraction</em> rather than
 * system storage. Exported files represent independent snapshots of the application's state.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Exporting doctor data</li>
 *     <li>Exporting patient data</li>
 *     <li>Exporting today's appointments</li>
 * </ul>
 * </p>
 */

public class ExportService {

    /**
     * Exports a list of doctors to a CSV file.
     *
     * @param doctors list of doctors to export
     * @param file    destination file
     */
    public static void exportDoctorsToFile(List<Doctor> doctors, File file) {
        write(file,
                "ID,FirstName,LastName,Email,Phone,Specialty,Hours",
                doctors.stream().map(d ->
                        d.getId() + "," +
                                d.getFirstName() + "," +
                                d.getLastName() + "," +
                                d.getEmail() + "," +
                                d.getPhone() + "," +
                                d.getSpeciality() + "," +
                                d.getWorkingHours()
                ).toList()
        );
    }

    /**
     * Exports a list of patients to a CSV file.
     *
     * @param patients list of patients to export
     * @param file     destination file
     */
    public static void exportPatientsToFile(List<Patient> patients, File file) {
        write(file,
                "ID,FirstName,LastName,Email,Phone,TaxID,DOB,Gender,History",
                patients.stream().map(p ->
                        p.getId() + "," +
                                p.getFirstName() + "," +
                                p.getLastName() + "," +
                                p.getEmail() + "," +
                                p.getPhone() + "," +
                                p.getTaxId() + "," +
                                p.getDob() + "," +
                                p.getGender() + "," +
                                p.getMedicalHistory()
                ).toList()
        );
    }

    /**
     * Exports only today's appointments to a CSV file.
     * Filters appointments by comparing their date to the current system date.
     *
     * @param list list of appointments
     * @param file destination file
     */
    public static void exportTodayAppointmentsToFile(List<Appointment> list, File file) {
        write(file,
                "ID,Patient,Doctor,DateTime,Reason,Status",
                list.stream()
                        .filter(a -> a.getDateTime().toLocalDate().equals(LocalDate.now()))
                        .map(a ->
                                a.getId() + "," +
                                        a.getPatient().getFullName() + "," +
                                        a.getDoctor().getFullName() + "," +
                                        a.getDateTime() + "," +
                                        a.getReason() + "," +
                                        a.getStatus()
                        ).toList()
        );
    }

    /**
     * Writes CSV content to a file.
     * This is an internal helper method used by all export functions.
     *
     * @param file   destination file
     * @param header CSV header row
     * @param rows   CSV data rows
     * @throws RuntimeException if writing to file fails
     */
    private static void write(File file, String header, List<String> rows) {
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println(header);
            rows.forEach(pw::println);
        } catch (Exception e) {
            throw new RuntimeException("Export failed", e);
        }
    }
}