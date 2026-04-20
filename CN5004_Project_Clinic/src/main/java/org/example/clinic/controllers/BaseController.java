package org.example.clinic.controllers;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

/**
 * This class provides shared UI utilities for all JavaFX controllers.
 * <p>
 * It centralizes common functionality used across multiple controllers,
 * such as dialog display, animations, and user confirmations.
 *
 * <p>
 * It is designed to reduce code duplication and ensure consistent UI behavior
 * throughout the application.
 */
public class BaseController {

    /**
     * Displays a warning alert dialog with the given message.
     *
     * @param message the message to display in the alert dialog
     */
    protected void show(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }

    /**
     * Applies a fade-in animation to the specified UI node.
     * <p>
     * The animation transitions the node from fully transparent to fully visible
     * over a fixed duration of 1 second.
     *
     * @param node the UI element to animate
     */
    protected void applyFade(Node node) {
        node.setOpacity(0);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Displays a confirmation dialog and returns the user's choice.
     *
     * @param message the confirmation message to display
     * @return {@code true} if the user selects YES, otherwise {@code false}
     */
    protected boolean confirm(String message) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                message,
                ButtonType.YES,
                ButtonType.NO
        );

        return alert.showAndWait()
                .orElse(ButtonType.NO) == ButtonType.YES;
    }
}