package com.srms.util;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * Toast notification system with animated slide-in/out.
 * Displays success, error, and warning messages.
 */
public class NotificationManager {

    /**
     * Show a success toast notification.
     */
    public static void showSuccess(StackPane root, String message) {
        showToast(root, "\u2705 " + message, "toast-success");
    }

    /**
     * Show an error toast notification.
     */
    public static void showError(StackPane root, String message) {
        showToast(root, "\u274C " + message, "toast-error");
    }

    /**
     * Show a warning toast notification.
     */
    public static void showWarning(StackPane root, String message) {
        showToast(root, "\u26A0 " + message, "toast-warning");
    }

    /**
     * Show an info toast notification.
     */
    public static void showInfo(StackPane root, String message) {
        showToast(root, "\u2139 " + message, "toast-info");
    }

    private static void showToast(StackPane root, String message, String styleClass) {
        Label toast = new Label(message);
        toast.getStyleClass().addAll("toast", styleClass);
        toast.setMaxWidth(400);
        toast.setWrapText(true);

        StackPane.setAlignment(toast, Pos.TOP_RIGHT);
        StackPane.setMargin(toast, new Insets(20, 20, 0, 0));

        // Start off-screen
        toast.setTranslateX(420);
        toast.setOpacity(0);

        root.getChildren().add(toast);

        // Slide in
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), toast);
        slideIn.setFromX(420);
        slideIn.setToX(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        ParallelTransition showAnim = new ParallelTransition(slideIn, fadeIn);

        // Slide out after delay
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToX(420);
        slideOut.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);

        ParallelTransition hideAnim = new ParallelTransition(slideOut, fadeOut);
        hideAnim.setOnFinished(e -> root.getChildren().remove(toast));

        SequentialTransition sequence = new SequentialTransition(showAnim, pause, hideAnim);
        sequence.play();
    }
}
