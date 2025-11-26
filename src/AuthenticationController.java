/**
 * AuthenticationController handles login and registration logic.
 * Validates credentials and creates user accounts.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class AuthenticationController {
    private DataManager dataManager;
    private MainApplication mainApp;
    
    private LoginView loginView;
    private RegisterView registerView;
    
    public AuthenticationController(DataManager dataManager, MainApplication mainApp) {
        this.dataManager = dataManager;
        this.mainApp = mainApp;
    }
    
    public void setView(LoginView loginView) {
        this.loginView = loginView;
    }
    
    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }
    
    /**
     * Handles login attempts.
     */
    public void handleLogin(String username, String password, String userType) {
        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            if (loginView != null) {
                loginView.showStatus("Please fill in all fields", "error");
            }
            return;
        }
        
        boolean isEmployee = "Employee".equals(userType);
        
        if (isEmployee) {
            Employee employee = dataManager.authenticateEmployee(username, password);
            if (employee != null) {
                mainApp.onLoginSuccess(null, employee);
            } else {
                if (loginView != null) {
                    loginView.showStatus("Invalid employee credentials", "error");
                }
            }
        } else {
            Customer customer = dataManager.authenticateCustomer(username, password);
            if (customer != null) {
                mainApp.onLoginSuccess(customer, null);
            } else {
                if (loginView != null) {
                    loginView.showStatus("Invalid customer credentials", "error");
                }
            }
        }
    }
    
    /**
     * Handles registration.
     */
    public void handleRegister(String username, String password, String confirmPassword,
                               String name, String userType, String employeeId) {
        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            name == null || name.trim().isEmpty()) {
            if (registerView != null) {
                registerView.showStatus("Please fill in all required fields", "error");
            }
            return;
        }
        
        if (password.length() < 4) {
            if (registerView != null) {
                registerView.showStatus("Password must be at least 4 characters", "error");
            }
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            if (registerView != null) {
                registerView.showStatus("Passwords do not match", "error");
            }
            return;
        }
        
        if (dataManager.usernameExists(username)) {
            if (registerView != null) {
                registerView.showStatus("Username already exists", "error");
            }
            return;
        }
        
        boolean isEmployee = "Employee".equals(userType);
        
        if (isEmployee) {
            if (employeeId == null || employeeId.trim().isEmpty()) {
                if (registerView != null) {
                    registerView.showStatus("Employee ID is required", "error");
                }
                return;
            }
            
            Employee newEmployee = new Employee(name, username, password, employeeId);
            if (dataManager.registerEmployee(newEmployee)) {
                if (registerView != null) {
                    registerView.showStatus("Account created! Please login.", "success");
                    registerView.clearFields();
                }
                mainApp.showLoginView();
            } else {
                if (registerView != null) {
                    registerView.showStatus("Error creating account", "error");
                }
            }
        } else {
            Customer newCustomer = new Customer(name, username, password);
            if (dataManager.registerCustomer(newCustomer)) {
                if (registerView != null) {
                    registerView.showStatus("Account created! Please login.", "success");
                    registerView.clearFields();
                }
                mainApp.showLoginView();
            } else {
                if (registerView != null) {
                    registerView.showStatus("Error creating account", "error");
                }
            }
        }
    }
    
    /**
     * Navigates to registration screen.
     */
    public void handleShowRegister() {
        mainApp.showRegisterView();
    }
    
    /**
     * Navigates to login screen.
     */
    public void handleShowLogin() {
        mainApp.showLoginView();
    }
}