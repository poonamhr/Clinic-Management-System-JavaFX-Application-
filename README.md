# Clinic-Management-System-JavaFX-Application-
# CN5004 – Advanced Programming

---

## Individual Assignment (2025–2026)

---

### **Student Information**

**Name:** Poonam Rani Kaur

**Student ID:** 2879196

**Module:** CN5004 – Advanced Programming

**Instructor:** Nektarios Deligiannakis

**Programming Language:** Java (JavaFX)

---


## Project Title

**Clinic Management System (JavaFX Application)**

---

## Project Description

This project presents a fully developed Clinic Management System built using Java and JavaFX. As stated in the original document, “This project implements a Clinic Management System for a medical clinic using Java and JavaFX.” The system follows Object-Oriented Programming (OOP) principles and provides a structured graphical user interface (GUI) that enables efficient management of doctors, patients, and appointments within a single integrated environment.

In addition, as highlighted in the original description, “The system is designed based on Object-Oriented Programming (OOP) principles and provides a graphical user interface (GUI).” This ensures that the application is both technically robust and user-friendly. The system also incorporates file-based data persistence, allowing all records to be stored permanently and reloaded when the application restarts, ensuring continuity and reliability of data.

The application is designed using a layered architecture inspired by the Model–View–Controller (MVC) pattern. This architectural approach enhances modularity, separates responsibilities effectively, and supports future scalability and maintainability of the system.

---

## Objectives

The primary aim of this project is to design and implement a reliable desktop application that demonstrates advanced programming concepts and structured software development practices. The system focuses on delivering both functional completeness and technical quality.

More specifically, the objectives include:

Developing a fully functional clinic management application using JavaFX
Applying core OOP principles such as encapsulation, abstraction, and modular design
Supporting management of patients, doctors, and appointments in an organized manner
Implementing validation mechanisms to ensure data accuracy and consistency
Preventing duplicate or conflicting appointment scheduling
Ensuring persistent storage using file handling techniques (CSV-based approach)
Providing a responsive and user-friendly interface with filtering and search capabilities
These objectives collectively ensure that the system is both practical in real-world use and academically aligned with module requirements.
---

## Structure

```
CN5004_Project_Clinic/
│
├── pom.xml                         # Maven configuration file
│
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java   # Java module configuration
│       │   │
│       │   └── org/example/clinic/
│       │       │
│       │       ├── App.java       # Main entry point of the application
│       │       │
│       │       ├── common/        # Utility and helper classes
│       │       │   ├── AppConstants.java    
│       │       │   ├── Utils.java            
│       │       │   └── ValidationUtils.java  
│       │       │
│       │       ├── controllers/   # JavaFX Controllers (UI Logic)
│       │       │   ├── BaseController.java
│       │       │   ├── MainController.java
│       │       │   ├── HomeController.java
│       │       │   ├── PatientController.java
│       │       │   ├── DoctorController.java
│       │       │   └── AppointmentController.java
│       │       │
│       │       ├── models/        # Core data models (OOP Entities)
│       │       │   ├── Patient.java
│       │       │   ├── Doctor.java
│       │       │   ├── Appointment.java
│       │       │   └── enums/
│       │       │       ├── AppointmentStatus.java
│       │       │       └── DoctorSpeciality.java
│       │       │
│       │       ├── persistence/   # File handling (Data storage)
│       │       │   └── FileManager.java
│       │       │
│       │       └── services/      # Business logic layer
│       │           ├── PatientService.java
│       │           ├── DoctorService.java
│       │           ├── AppointmentService.java
│       │           └── ExportService.java
│       │
│       └── resources/
│           └── org/example/clinic/
│               ├── css/
│               │   └── style.css        # Application styling
│               │
│               ├── images/
│               │   └── index.png        # UI image
│               │
│               └── views/               # JavaFX FXML UI files
│                   ├── main.fxml
│                   ├── home.fxml
│                   ├── patient.fxml
│                   ├── doctor.fxml
│                   └── appointment.fxml
```

---



The project is organized into clearly defined packages, each responsible for a specific aspect of the system. This modular design improves readability, simplifies debugging, and allows independent development of components.

The structure includes:

* **Models** representing core entities (Patient, Doctor, Appointment)
* **Controllers** managing UI logic and handling user interactions
* **Services** implementing business logic and enforcing system rules
* **Persistence** handling file input/output operations
* **Common utilities** providing reusable helper methods and constants
* **FXML views and CSS** defining the graphical user interface

This structured approach ensures a clean separation of concerns and enhances the overall maintainability of the application.

---

## **System Architecture**

The system adopts a layered architecture based on the **MVC design pattern**, ensuring clear separation between data, logic, and presentation.

* **Models** encapsulate data and represent real-world entities within the system
* **Views (FXML)** define the layout and visual structure of the application
* **Controllers** manage user interactions and connect the UI to backend logic
* **Services** implement business rules such as validation, CRUD operations, and scheduling logic
* **Persistence layer** handles CSV-based data storage and retrieval
* **Common utilities** provide shared functionality used across multiple components

This architecture not only improves code organization but also allows the system to be extended or modified with minimal impact on other components.

---

## **Application Entry Point (App.java)**

The `App.java` class serves as the central entry point of the application and is responsible for initializing the JavaFX runtime environment. It ensures that all necessary resources and configurations are prepared before the user interacts with the system.

The execution begins in the `main()` method, which launches the JavaFX application. The `start(Stage primaryStage)` method then initializes the main window, loads the primary interface, and applies global styling.

In addition, the class ensures that required directories for data storage exist before the application starts, preventing runtime errors related to missing files or paths. It also manages scene transitions and resource loading through helper methods such as `loadScene()` and `loadNode()`.

These responsibilities make `App.java` a critical component that guarantees smooth startup, consistent navigation, and proper resource management throughout the application lifecycle.

---

## **Core Utilities (Common Package)**

The **common** package contains reusable utility classes that support the entire system and promote code reusability.

* **AppConstants** centralizes configuration values such as file paths, window dimensions, and resource locations, eliminating hard-coded values and improving maintainability.
* **Utils** provides helper methods for string manipulation, including normalization and capitalization, ensuring consistent formatting of user input.
* **ValidationUtils** implements structured validation rules using regular expressions and logical checks to ensure that all input data is valid before processing.

Together, these utilities enhance reliability, consistency, and maintainability across the application.

---

## **Domain Model (Models Package)**

The models define the core data structures of the system and reflect real-world clinical entities.

* The **Doctor** class manages doctor-related information and uses the `DoctorSpeciality` enum to enforce valid specialization values.
* The **Patient** class stores personal and medical details, ensuring proper formatting and accurate handling of dates using `LocalDate`.
* The **Appointment** class acts as a central link between doctors and patients, using `LocalDateTime` for scheduling and `AppointmentStatus` to track appointment states.

This structured modeling approach ensures strong data integrity and forms the foundation for business logic operations.

---

## **Data Persistence Layer**

The persistence layer is implemented through the `FileManager` class, which manages all file operations using a CSV-based approach. As stated in the original document, *“The FileManager acts as a bridge between application data and permanent storage.”*

It provides methods for reading and writing data, ensuring directories exist, and safely handling missing files. This approach offers a simple yet effective solution for persistent storage without the need for a database, while still maintaining reliability and efficiency.

---

## **Business Logic Layer (Services)**

The Services layer contains the core functionality of the application and ensures that all business rules are enforced consistently.

* **PatientService and DoctorService** handle CRUD operations, input validation, and unique ID generation
* **AppointmentService** manages scheduling, prevents conflicts, and ensures that no overlapping appointments occur
* **ExportService** supports data export functionality for reporting and backup purposes

This layer acts as an intermediary between the UI and the data layer, ensuring clean separation and logical consistency.

---

## **Presentation Layer (Controllers)**

Controllers manage user interaction and coordinate communication between the UI and the services layer.

* **MainController** handles navigation between different views
* **HomeController** manages dashboard initialization and user actions
* **BaseController** provides shared utilities such as alerts and confirmations
* **DoctorController and PatientController** handle form input, validation, and data display
* **AppointmentController** manages scheduling, validation, and real-time feedback

This design ensures that user actions are processed efficiently and reflected accurately in the system.

---

## **User Interface (Views – JavaFX)**

The user interface is built using JavaFX FXML files and styled with a centralized CSS file to maintain consistency across the application.

Each view corresponds to a specific functional area:

* `main.fxml` – main layout and navigation
* `home.fxml` – dashboard overview
* `doctor.fxml` – doctor management
* `patient.fxml` – patient management
* `appointment.fxml` – appointment scheduling

The interface is designed to be intuitive and user-friendly, allowing users to interact with the system efficiently.

---

# **Conclusion**

This project demonstrates the successful development of a structured and fully functional Clinic Management System using JavaFX. By applying OOP principles and an MVC-inspired architecture, the system achieves a high level of modularity, maintainability, and scalability.

The integration of file-based persistence, robust validation mechanisms, and conflict-free scheduling ensures data reliability and system correctness. Furthermore, the clean and responsive user interface enhances usability and overall user experience.

