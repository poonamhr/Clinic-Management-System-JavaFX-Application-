package org.example.clinic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.clinic.common.AppConstants;
import org.example.clinic.persistence.FileManager;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Main entry point of the Clinic Management System JavaFX application.
 *
 * <p>
 * This class is responsible for:
 * <ul>
 *     <li>Initializing the primary application {@link Stage}</li>
 *     <li>Managing scene transitions and FXML loading</li>
 *     <li>Providing utility methods for resource handling</li>
 *     <li>Supporting platform‑specific configurations</li>
 * </ul>
 * </p>
 *
 * <p>
 * It also exposes global static helper methods to simplify UI navigation
 * and resource management across the application.
 * </p>
 */

public class App extends Application {

    // Primary JavaFX stage used throughout the application lifecycle.
    private static Stage stage;

    // Desktop instance used for interacting with the host operating system.
    private static Desktop desktop;

    //Static initializer to configure desktop support.
    static {
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
    }

    /**
     * Loads a new scene from an FXML file and applies the main stylesheet.
     *
     * @param fxml   name of the FXML file (without extension)
     * @param width  scene width
     * @param height scene height
     */
    public static void loadScene(String fxml, double width, double height) {
        Parent pane = (Parent) loadNode(fxml);
        Scene scene = new Scene(pane, width, height);
        getStage().setScene(scene);

        scene.getStylesheets().add(
                getResource(AppConstants.CSS_BASE_PATH + "style.css").toExternalForm()
        );
    }

    /**
     * Loads an FXML file and returns its root node.
     *
     * @param fxml name of the FXML file (without extension)
     * @return the root node of the loaded FXML
     * @throws RuntimeException if the FXML file cannot be found or loaded
     */
    public static Node loadNode(String fxml) {
        try {
            return FXMLLoader.load(
                    getResource(String.format("%s%s.fxml", AppConstants.FXML_BASE_PATH, fxml))
            );
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML file: " + fxml, e);
        }
    }

    /**
     * Returns a resource from the application's classpath.
     *
     * @param path resource path
     * @return URL of the resource
     */
    public static URL getResource(String path) {
        return Objects.requireNonNull(
                App.class.getResource(path),
                "Resource not found: " + path
        );
    }

    /**
     * Returns the primary application stage.
     *
     * @return the global {@link Stage} instance
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Returns the system {@link Desktop} instance if supported.
     *
     * @return the {@link Desktop} instance, or {@code null} if not supported
     */
    public static @Nullable Desktop getDesktop() {
        return desktop;
    }


    /**
     * Called when the application starts.
     * Sets up the stage, loads fonts, ensures data folders exist,
     * and shows the main window.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // Initialize data storage
        FileManager.ensureDirectoryExists(AppConstants.DATA_PATH);



        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);

        loadScene("main", AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);

        stage.show();
    }

    /**
     * Application entry point.
     * Launches the JavaFX runtime, which in turn calls {@link #start(Stage)}.
     */
    public static void main(String[] args) {
        launch();
    }
}