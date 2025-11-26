import javafx.scene.control.Alert;

/**
 * LoginController handles authentication logic for both customers and employees.
 * Now properly uses User, Customer, and Employee objects.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class LoginController {
    private MainApplication mainApp;
    private DataManager dataManager;
    private LoginView loginView;
    private RegisterView registerView;
    
    public LoginController(MainApplication mainApp, DataManager dataManager) {
        this.mainApp = mainApp;
        this.dataManager = dataManager;
    }
    
    /**
     * Handles login attempts for both customers and employees.
     * Called from LoginView.
     */
    public void handleLogin(String username, String password, String userType) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            showAlert("Login Failed", "Please fill in all fields.", Alert.AlertType.WARNING);
            return;
        }
        
        boolean isEmployee = "Employee".equals(userType);
        
        if (isEmployee) {
            Employee employee = dataManager.authenticateEmployee(username, password);
            if (employee != null) {
                mainApp.onLoginSuccess(null, employee);
            } else {
                showAlert("Login Failed", "Invalid employee credentials.", Alert.AlertType.ERROR);
                if (loginView != null) {
                    loginView.showStatus("Invalid credentials", "error");
                }
            }
        } else {
            Customer customer = dataManager.authenticateCustomer(username, password);
            if (customer != null) {
                mainApp.onLoginSuccess(customer, null);
            } else {
                showAlert("Login Failed", "Invalid customer credentials.", Alert.AlertType.ERROR);
                if (loginView != null) {
                    loginView.showStatus("Invalid credentials", "error");
                }
            }
        }
    }
    
    /**
     * Handles registration from RegisterView.
     * Routes to appropriate registration method based on user type.
     */
    public void handleRegister(String username, String password, String name, 
                               String userType, String employeeId) {
        boolean isEmployee = "Employee".equals(userType);
        
        if (isEmployee) {
            handleEmployeeRegistration(username, password, password, name, employeeId);
        } else {
            handleCustomerRegistration(username, password, password, name);
        }
    }
    
    /**
     * Handles customer registration using Customer object.
     */
    private void handleCustomerRegistration(String username, String password, 
                                           String confirmPassword, String name) {
        // Validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            showAlert("Registration Failed", "Please fill in all fields.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Please fill in all fields", "error");
            }
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Passwords do not match", "error");
            }
            return;
        }
        
        if (dataManager.usernameExists(username)) {
            showAlert("Registration Failed", "Username already exists.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Username already exists", "error");
            }
            return;
        }
        
        // Create Customer object
        Customer newCustomer = new Customer(name, username, password);
        
        // Save to file
        if (dataManager.registerCustomer(newCustomer)) {
            showAlert("Registration Successful", 
                     "Customer account created successfully!\nYou can now login.", 
                     Alert.AlertType.INFORMATION);
            if (registerView != null) {
                registerView.showStatus("Account created! Please login.", "success");
                registerView.clearFields();
            }
            mainApp.showLoginView();
        } else {
            showAlert("Registration Failed", "Error saving customer data.", Alert.AlertType.ERROR);
            if (registerView != null) {
                registerView.showStatus("Error creating account", "error");
            }
        }
    }
    
    /**
     * Handles employee registration using Employee object.
     */
    private void handleEmployeeRegistration(String username, String password, 
                                           String confirmPassword, String name, 
                                           String employeeID) {
        // Validation
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty() ||
            employeeID == null || employeeID.trim().isEmpty()) {
            showAlert("Registration Failed", "Please fill in all fields.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Please fill in all fields", "error");
            }
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Passwords do not match", "error");
            }
            return;
        }
        
        if (dataManager.usernameExists(username)) {
            showAlert("Registration Failed", "Username already exists.", Alert.AlertType.WARNING);
            if (registerView != null) {
                registerView.showStatus("Username already exists", "error");
            }
            return;
        }
        
        // Create Employee object
        Employee newEmployee = new Employee(name, username, password, employeeID);
        
        // Save to file
        if (dataManager.registerEmployee(newEmployee)) {
            showAlert("Registration Successful", 
                     "Employee account created successfully!\nYou can now login.", 
                     Alert.AlertType.INFORMATION);
            if (registerView != null) {
                registerView.showStatus("Account created! Please login.", "success");
                registerView.clearFields();
            }
            mainApp.showLoginView();
        } else {
            showAlert("Registration Failed", "Error saving employee data.", Alert.AlertType.ERROR);
            if (registerView != null) {
                registerView.showStatus("Error creating account", "error");
            }
        }
    }
    
    /**
     * Navigates to registration view.
     */
    public void handleShowRegister() {
        mainApp.showRegisterView();
    }
    
    /**
     * Navigates to login view.
     */
    public void handleShowLogin() {
        mainApp.showLoginView();
    }
    
    /**
     * Shows an alert dialog.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }
    
    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }
}