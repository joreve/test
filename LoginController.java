import javafx.scene.control.Alert;

/**
 * LoginController handles authentication and registration logic.
 * Manages user login, registration, and session management.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class LoginController {
    private DataPersistence dataPersistence;
    private LoginView loginView;
    private RegisterView registerView;
    private MainApplication mainApp;
    
    /**
     * Constructs a LoginController.
     *
     * @param mainApp the main application reference
     */
    public LoginController(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.dataPersistence = new DataPersistence();
    }
    
    /**
     * Sets the login view.
     *
     * @param loginView the login view
     */
    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }
    
    /**
     * Sets the register view.
     *
     * @param registerView the register view
     */
    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }
    
    /**
     * Handles user login.
     *
     * @param username the username
     * @param password the password
     * @param userType "Customer" or "Employee"
     */
    public void handleLogin(String username, String password, String userType) {
        if ("Customer".equals(userType)) {
            Customer customer = dataPersistence.authenticateCustomer(username, password);
            if (customer != null) {
                loginView.showStatus("Login successful! Welcome " + customer.getName(), "success");
                
                // Delay to show success message
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            mainApp.onLoginSuccess(customer, null);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                loginView.showStatus("Invalid username or password", "error");
            }
        } else if ("Employee".equals(userType)) {
            Employee employee = dataPersistence.authenticateEmployee(username, password);
            if (employee != null) {
                loginView.showStatus("Login successful! Welcome " + employee.getName(), "success");
                
                // Delay to show success message
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            mainApp.onLoginSuccess(null, employee);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                loginView.showStatus("Invalid username or password", "error");
            }
        }
    }
    
    /**
     * Handles user registration.
     *
     * @param username the username
     * @param password the password
     * @param name the full name
     * @param userType "Customer" or "Employee"
     * @param employeeId the employee ID (for employees only)
     */
    public void handleRegister(String username, String password, String name, 
                               String userType, String employeeId) {
        // Check if username already exists
        if (dataPersistence.usernameExists(username)) {
            registerView.showStatus("Username already exists. Please choose another.", "error");
            return;
        }
        
        boolean success = false;
        if ("Customer".equals(userType)) {
            success = dataPersistence.registerCustomer(username, password, name);
        } else if ("Employee".equals(userType)) {
            success = dataPersistence.registerEmployee(username, password, name, employeeId);
        }
        
        if (success) {
            registerView.showStatus("Account created successfully! Redirecting to login...", "success");
            
            // Delay then show login
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(() -> {
                        handleShowLogin();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            registerView.showStatus("Registration failed. Please try again.", "error");
        }
    }
    
    /**
     * Shows the login view.
     */
    public void handleShowLogin() {
        if (registerView != null) {
            registerView.clearFields();
        }
        mainApp.showLoginView();
    }
    
    /**
     * Shows the register view.
     */
    public void handleShowRegister() {
        if (loginView != null) {
            loginView.clearFields();
        }
        mainApp.showRegisterView();
    }
    
    /**
     * Gets the data persistence instance.
     *
     * @return the data persistence
     */
    public DataPersistence getDataPersistence() {
        return dataPersistence;
    }
}