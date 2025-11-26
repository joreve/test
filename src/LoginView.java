import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoginView displays the login interface.
 * NO controller creation - controller is injected by MainApplication.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class LoginView extends BorderPane {
    private AuthenticationController controller;
    
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> userTypeCombo;
    private Button loginButton;
    private Button registerButton;
    private Label statusLabel;
    
    public LoginView() {
        initializeUI();
    }
    
    /**
     * Injects the controller after view creation.
     */
    public void setController(AuthenticationController controller) {
        this.controller = controller;
    }
    
    private void initializeUI() {
        setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");
        
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(50));
        centerBox.setMaxWidth(450);
        
        Label titleLabel = new Label("11-Seven");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Label subtitleLabel = new Label("Convenience Store Management System");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #f0f0f0;");
        
        VBox headerBox = new VBox(10, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(30));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        formBox.setAlignment(Pos.CENTER);
        
        Label formTitle = new Label("Login");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label userTypeLabel = new Label("Login as:");
        userTypeLabel.setFont(Font.font("Arial", 14));
        
        userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Customer", "Employee");
        userTypeCombo.setValue("Customer");
        userTypeCombo.setPrefWidth(350);
        userTypeCombo.setStyle("-fx-font-size: 14px;");
        
        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", 14));
        
        usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(350);
        usernameField.setStyle("-fx-font-size: 14px;");
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", 14));
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(350);
        passwordField.setStyle("-fx-font-size: 14px;");
        
        passwordField.setOnAction(e -> handleLogin());
        
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(350);
        statusLabel.setAlignment(Pos.CENTER);
        
        loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                            "-fx-font-size: 16px; -fx-font-weight: bold;");
        loginButton.setPrefWidth(350);
        loginButton.setPrefHeight(45);
        loginButton.setOnAction(e -> handleLogin());
        
        Separator separator = new Separator();
        separator.setPrefWidth(350);
        
        Label registerLabel = new Label("Don't have an account?");
        registerLabel.setFont(Font.font("Arial", 12));
        
        registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; " +
                               "-fx-font-size: 14px;");
        registerButton.setPrefWidth(350);
        registerButton.setPrefHeight(40);
        registerButton.setOnAction(e -> controller.handleShowRegister());
        
        formBox.getChildren().addAll(
            formTitle,
            userTypeLabel, userTypeCombo,
            usernameLabel, usernameField,
            passwordLabel, passwordField,
            statusLabel,
            loginButton,
            separator,
            registerLabel,
            registerButton
        );
        
        centerBox.getChildren().addAll(headerBox, formBox);
        
        StackPane centerPane = new StackPane(centerBox);
        setCenter(centerPane);
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String userType = userTypeCombo.getValue();
        
        controller.handleLogin(username, password, userType);
    }
    
    /**
     * Shows a status message.
     * Called by controller.
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
        statusLabel.setText("");
        userTypeCombo.setValue("Customer");
    }
}