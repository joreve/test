import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * RegisterView displays the registration interface for new users.
 * Allows customers and employees to create accounts.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class RegisterView extends BorderPane {
    private LoginController controller;
    
    // UI Components
    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField nameField;
    private TextField employeeIdField;
    private ComboBox<String> userTypeCombo;
    private Button registerButton;
    private Button backButton;
    private Label statusLabel;
    private VBox employeeFields;
    
    /**
     * Constructs a RegisterView with the specified controller.
     *
     * @param controller the login controller
     */
    public RegisterView(LoginController controller) {
        this.controller = controller;
        initializeUI();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        centerBox.setMaxWidth(450);
        
        // Title
        Label titleLabel = new Label("Create Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        VBox headerBox = new VBox(10, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        // Registration form
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        formBox.setAlignment(Pos.CENTER);
        
        // User type selection
        Label userTypeLabel = new Label("Register as:");
        userTypeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Customer", "Employee");
        userTypeCombo.setValue("Customer");
        userTypeCombo.setPrefWidth(350);
        userTypeCombo.setStyle("-fx-font-size: 14px;");
        userTypeCombo.setOnAction(e -> toggleEmployeeFields());
        
        // Name field
        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("Arial", 14));
        
        nameField = new TextField();
        nameField.setPromptText("Enter full name");
        nameField.setPrefWidth(350);
        nameField.setStyle("-fx-font-size: 14px;");
        
        // Username field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", 14));
        
        usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setPrefWidth(350);
        usernameField.setStyle("-fx-font-size: 14px;");
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", 14));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Choose a password");
        passwordField.setPrefWidth(350);
        passwordField.setStyle("-fx-font-size: 14px;");
        
        // Confirm password field
        Label confirmPasswordLabel = new Label("Confirm Password:");
        confirmPasswordLabel.setFont(Font.font("Arial", 14));
        
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefWidth(350);
        confirmPasswordField.setStyle("-fx-font-size: 14px;");
        
        // Employee-specific fields (hidden by default)
        employeeFields = new VBox(10);
        employeeFields.setAlignment(Pos.CENTER_LEFT);
        employeeFields.setManaged(false);
        employeeFields.setVisible(false);
        
        Label employeeIdLabel = new Label("Employee ID:");
        employeeIdLabel.setFont(Font.font("Arial", 14));
        
        employeeIdField = new TextField();
        employeeIdField.setPromptText("Enter employee ID");
        employeeIdField.setPrefWidth(350);
        employeeIdField.setStyle("-fx-font-size: 14px;");
        
        employeeFields.getChildren().addAll(employeeIdLabel, employeeIdField);
        
        // Status label
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(350);
        statusLabel.setAlignment(Pos.CENTER);
        
        // Buttons
        registerButton = new Button("Create Account");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                               "-fx-font-size: 16px; -fx-font-weight: bold;");
        registerButton.setPrefWidth(350);
        registerButton.setPrefHeight(45);
        registerButton.setOnAction(e -> handleRegister());
        
        backButton = new Button("← Back to Login");
        backButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #666; " +
                           "-fx-font-size: 14px; -fx-cursor: hand;");
        backButton.setPrefWidth(350);
        backButton.setOnAction(e -> controller.handleShowLogin());
        
        formBox.getChildren().addAll(
            userTypeLabel, userTypeCombo,
            nameLabel, nameField,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            confirmPasswordLabel, confirmPasswordField,
            employeeFields,
            statusLabel,
            registerButton,
            backButton
        );
        
        centerBox.getChildren().addAll(headerBox, formBox);
        
        StackPane centerPane = new StackPane(centerBox);
        setCenter(centerPane);
    }
    
    /**
     * Toggles employee-specific fields based on user type.
     */
    private void toggleEmployeeFields() {
        boolean isEmployee = "Employee".equals(userTypeCombo.getValue());
        employeeFields.setVisible(isEmployee);
        employeeFields.setManaged(isEmployee);
    }
    
    /**
     * Handles the register button click.
     */
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String name = nameField.getText().trim();
        String userType = userTypeCombo.getValue();
        String employeeId = employeeIdField.getText().trim();
        
        // Validation
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all required fields", "error");
            return;
        }
        
        if (password.length() < 4) {
            showStatus("Password must be at least 4 characters", "error");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match", "error");
            return;
        }
        
        if ("Employee".equals(userType) && employeeId.isEmpty()) {
            showStatus("Employee ID is required", "error");
            return;
        }
        
        controller.handleRegister(username, password, name, userType, employeeId);
    }
    
    /**
     * Shows a status message.
     *
     * @param message the message to display
     * @param type "success", "error", or "info"
     */
    public void showStatus(String message, String type) {
        statusLabel.setText(message);
        
        switch (type.toLowerCase()) {
            case "success":
                statusLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                break;
            case "error":
                statusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
                break;
            default:
                statusLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
        }
    }
    
    /**
     * Clears all input fields.
     */
    public void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        nameField.clear();
        employeeIdField.clear();
        statusLabel.setText("");
        userTypeCombo.setValue("Customer");
        toggleEmployeeFields();
    }
}