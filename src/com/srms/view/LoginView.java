package com.srms.view;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.Duration;

/**
 * Premium login screen with glassmorphism design and animations.
 */
public class LoginView {
    private final StackPane root;
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Label errorLabel;
    private final VBox loginCard;

    public LoginView() {
        // Background gradient
        root = new StackPane();
        root.getStyleClass().add("login-background");

        // Decorative circles
        Circle c1 = new Circle(150);
        c1.setFill(Color.web("#667eea", 0.15));
        StackPane.setAlignment(c1, Pos.TOP_LEFT);
        StackPane.setMargin(c1, new Insets(-50, 0, 0, -50));

        Circle c2 = new Circle(200);
        c2.setFill(Color.web("#764ba2", 0.1));
        StackPane.setAlignment(c2, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(c2, new Insets(0, -80, -80, 0));

        Circle c3 = new Circle(100);
        c3.setFill(Color.web("#f093fb", 0.08));
        StackPane.setAlignment(c3, Pos.CENTER_RIGHT);
        StackPane.setMargin(c3, new Insets(0, 100, 200, 0));

        // Login card
        loginCard = new VBox(20);
        loginCard.getStyleClass().add("login-card");
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(420);
        loginCard.setMaxHeight(500);
        loginCard.setPadding(new Insets(50, 40, 50, 40));

        // Logo/icon area
        StackPane logoCircle = new StackPane();
        logoCircle.getStyleClass().add("logo-circle");
        logoCircle.setMaxSize(80, 80);
        logoCircle.setMinSize(80, 80);
        Label logoIcon = new Label("\uD83C\uDF93");
        logoIcon.setStyle("-fx-font-size: 36px;");
        logoCircle.getChildren().add(logoIcon);

        // Title
        Label title = new Label("SRMS");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Student Record Management System");
        subtitle.getStyleClass().add("login-subtitle");

        // Username field
        VBox usernameBox = new VBox(6);
        Label usernameLabel = new Label("Username");
        usernameLabel.getStyleClass().add("field-label");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("login-field");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(6);
        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("login-field");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Error label
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Login button
        loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("login-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        // Default credentials hint
        Label hint = new Label("Default: admin / admin123");
        hint.getStyleClass().add("login-hint");

        loginCard.getChildren().addAll(logoCircle, title, subtitle, usernameBox, passwordBox, errorLabel, loginButton, hint);

        root.getChildren().addAll(c1, c2, c3, loginCard);

        // Entry animation
        playEntryAnimation();
    }

    private void playEntryAnimation() {
        loginCard.setOpacity(0);
        loginCard.setTranslateY(40);

        FadeTransition fade = new FadeTransition(Duration.millis(800), loginCard);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setInterpolator(Interpolator.EASE_OUT);

        TranslateTransition slide = new TranslateTransition(Duration.millis(800), loginCard);
        slide.setFromY(40);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        new ParallelTransition(fade, slide).play();
    }

    /**
     * Play shake animation on error.
     */
    public void shakeOnError() {
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);

        TranslateTransition shake = new TranslateTransition(Duration.millis(80), loginCard);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> loginCard.setTranslateX(0));
        shake.play();
    }

    // Getters
    public StackPane getRoot() { return root; }
    public TextField getUsernameField() { return usernameField; }
    public PasswordField getPasswordField() { return passwordField; }
    public Button getLoginButton() { return loginButton; }
    public Label getErrorLabel() { return errorLabel; }
}
