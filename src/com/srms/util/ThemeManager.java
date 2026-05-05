package com.srms.util;

import javafx.scene.Scene;
import java.io.File;

/**
 * Manages dark/light theme toggling for the application.
 */
public class ThemeManager {
    private static boolean darkMode = false;
    private static final String LIGHT_CSS = "light-theme.css";
    private static final String DARK_CSS = "dark-theme.css";

    public static boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Apply the current theme to a scene.
     */
    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        String cssFile = darkMode ? DARK_CSS : LIGHT_CSS;
        String cssPath = getCSSPath(cssFile);
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        }
    }

    /**
     * Toggle between dark and light mode.
     */
    public static void toggleTheme(Scene scene) {
        darkMode = !darkMode;
        applyTheme(scene);
    }

    /**
     * Set a specific theme.
     */
    public static void setDarkMode(Scene scene, boolean dark) {
        darkMode = dark;
        applyTheme(scene);
    }

    private static String getCSSPath(String filename) {
        try {
            File cssFile = new File("resources/css/" + filename);
            if (cssFile.exists()) {
                return cssFile.toURI().toURL().toExternalForm();
            }
            // Fallback: try classpath
            var url = ThemeManager.class.getResource("/css/" + filename);
            if (url != null) return url.toExternalForm();
        } catch (Exception e) {
            System.err.println("[Theme] Failed to load CSS: " + e.getMessage());
        }
        return null;
    }
}
