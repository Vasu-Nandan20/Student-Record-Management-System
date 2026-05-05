package com.srms.controller;

import com.srms.model.User;
import com.srms.service.AuthService;
import com.srms.view.LoginView;
import javafx.scene.input.KeyCode;

/**
 * Controller for the login screen.
 * Handles authentication logic and login events.
 */
public class LoginController {
    private final LoginView view;
    private final AuthService authService;
    private Runnable onLoginSuccess;
    private User loggedInUser;

    public LoginController() {
        this.view = new LoginView();
        this.authService = new AuthService();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        // Login button click
        view.getLoginButton().setOnAction(e -> attemptLogin());

        // Enter key press
        view.getPasswordField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) attemptLogin();
        });
        view.getUsernameField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) view.getPasswordField().requestFocus();
        });
    }

    private void attemptLogin() {
        String username = view.getUsernameField().getText().trim();
        String password = view.getPasswordField().getText();

        if (username.isEmpty() || password.isEmpty()) {
            view.getErrorLabel().setText("Please enter both username and password");
            view.shakeOnError();
            return;
        }

        view.getLoginButton().setDisable(true);
        view.getLoginButton().setText("Signing in...");

        try {
            User user = authService.login(username, password);
            if (user != null) {
                loggedInUser = user;
                if (onLoginSuccess != null) onLoginSuccess.run();
            } else {
                view.getErrorLabel().setText("Invalid username or password");
                view.shakeOnError();
                view.getPasswordField().clear();
            }
        } catch (Exception e) {
            view.getErrorLabel().setText("Connection error: " + e.getMessage());
            view.shakeOnError();
        } finally {
            view.getLoginButton().setDisable(false);
            view.getLoginButton().setText("Sign In");
        }
    }

    public void setOnLoginSuccess(Runnable callback) { this.onLoginSuccess = callback; }
    public User getLoggedInUser() { return loggedInUser; }
    public LoginView getView() { return view; }
}
