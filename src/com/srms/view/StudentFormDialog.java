package com.srms.view;

import com.srms.model.Student;
import com.srms.util.Validator;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Optional;

/**
 * Modal dialog for adding or editing a student with real-time validation.
 */
public class StudentFormDialog {
    private final Dialog<Student> dialog;
    private final TextField nameField, ageField, courseField, marksField, emailField, phoneField;
    private final Label nameError, ageError, courseError, marksError, emailError, phoneError;
    private final Button saveButton;
    private final Student existingStudent;

    /**
     * Create a dialog for adding a new student.
     */
    public static Optional<Student> showAddDialog() {
        StudentFormDialog form = new StudentFormDialog(null);
        return form.dialog.showAndWait();
    }

    /**
     * Create a dialog for editing an existing student.
     */
    public static Optional<Student> showEditDialog(Student student) {
        StudentFormDialog form = new StudentFormDialog(student);
        return form.dialog.showAndWait();
    }

    private StudentFormDialog(Student student) {
        this.existingStudent = student;
        boolean isEdit = student != null;

        dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Student" : "Add New Student");
        dialog.setHeaderText(isEdit ? "\u270F Edit Student Record" : "\u2795 Add New Student Record");
        dialog.getDialogPane().getStyleClass().add("student-form-dialog");

        // Buttons
        ButtonType saveType = new ButtonType(isEdit ? "Update" : "Add Student", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        saveButton = (Button) dialog.getDialogPane().lookupButton(saveType);
        saveButton.getStyleClass().add("btn-primary");

        // Form grid
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(25));
        grid.setPrefWidth(500);

        // Fields
        nameField = createField("Full Name", "e.g., John Doe");
        ageField = createField("Age", "e.g., 20");
        courseField = createField("Course", "e.g., Computer Science");
        marksField = createField("Marks (0-100)", "e.g., 85.5");
        emailField = createField("Email (optional)", "e.g., john@email.com");
        phoneField = createField("Phone (optional)", "e.g., 9876543210");

        // Error labels
        nameError = createErrorLabel();
        ageError = createErrorLabel();
        courseError = createErrorLabel();
        marksError = createErrorLabel();
        emailError = createErrorLabel();
        phoneError = createErrorLabel();

        // Layout
        int row = 0;
        addFormRow(grid, row++, "Name *", nameField, nameError);
        addFormRow(grid, row++, "Age *", ageField, ageError);
        addFormRow(grid, row++, "Course *", courseField, courseError);
        addFormRow(grid, row++, "Marks *", marksField, marksError);
        addFormRow(grid, row++, "Email", emailField, emailError);
        addFormRow(grid, row++, "Phone", phoneField, phoneError);

        // Pre-fill for edit mode
        if (isEdit) {
            nameField.setText(student.getName());
            ageField.setText(String.valueOf(student.getAge()));
            courseField.setText(student.getCourse());
            marksField.setText(String.valueOf(student.getMarks()));
            emailField.setText(student.getEmail() != null ? student.getEmail() : "");
            phoneField.setText(student.getPhone() != null ? student.getPhone() : "");
        }

        // Real-time validation
        nameField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validateName, nameError, nameField));
        ageField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validateAge, ageError, ageField));
        courseField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validateCourse, courseError, courseField));
        marksField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validateMarks, marksError, marksField));
        emailField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validateEmail, emailError, emailField));
        phoneField.textProperty().addListener((o, ov, nv) -> validateField(nv, Validator::validatePhone, phoneError, phoneField));

        dialog.getDialogPane().setContent(grid);

        // Prevent close on validation error
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAll()) {
                event.consume();
            }
        });

        // Convert result
        dialog.setResultConverter(buttonType -> {
            if (buttonType == saveType) {
                Student s = new Student();
                if (isEdit) s.setId(student.getId());
                s.setName(nameField.getText().trim());
                s.setAge(Integer.parseInt(ageField.getText().trim()));
                s.setCourse(courseField.getText().trim());
                s.setMarks(Double.parseDouble(marksField.getText().trim()));
                s.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                s.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                return s;
            }
            return null;
        });

        // Apply dialog styling
        dialog.getDialogPane().setPrefWidth(550);
    }

    private TextField createField(String label, String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("form-field");
        return field;
    }

    private Label createErrorLabel() {
        Label lbl = new Label();
        lbl.getStyleClass().add("field-error");
        lbl.setVisible(false);
        lbl.setManaged(false);
        return lbl;
    }

    private void addFormRow(GridPane grid, int row, String labelText, TextField field, Label errorLabel) {
        Label label = new Label(labelText);
        label.getStyleClass().add("form-label");
        grid.add(label, 0, row * 2);
        grid.add(field, 1, row * 2);
        grid.add(errorLabel, 1, row * 2 + 1);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    @FunctionalInterface
    private interface ValidationFunction {
        String validate(String value);
    }

    private void validateField(String value, ValidationFunction validator, Label errorLabel, TextField field) {
        String error = validator.validate(value);
        if (error != null) {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            if (!field.getStyleClass().contains("field-invalid")) field.getStyleClass().add("field-invalid");
        } else {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            field.getStyleClass().remove("field-invalid");
        }
    }

    private boolean validateAll() {
        boolean valid = true;
        String e;

        e = Validator.validateName(nameField.getText());
        if (e != null) { validateField(nameField.getText(), Validator::validateName, nameError, nameField); valid = false; }

        e = Validator.validateAge(ageField.getText());
        if (e != null) { validateField(ageField.getText(), Validator::validateAge, ageError, ageField); valid = false; }

        e = Validator.validateCourse(courseField.getText());
        if (e != null) { validateField(courseField.getText(), Validator::validateCourse, courseError, courseField); valid = false; }

        e = Validator.validateMarks(marksField.getText());
        if (e != null) { validateField(marksField.getText(), Validator::validateMarks, marksError, marksField); valid = false; }

        e = Validator.validateEmail(emailField.getText());
        if (e != null) { validateField(emailField.getText(), Validator::validateEmail, emailError, emailField); valid = false; }

        e = Validator.validatePhone(phoneField.getText());
        if (e != null) { validateField(phoneField.getText(), Validator::validatePhone, phoneError, phoneField); valid = false; }

        return valid;
    }
}
