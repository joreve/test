/**
 * LoginController handles authentication and registration logic.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class LoginController {
    private DataManager dataManager;
    private LoginView loginView;
    private RegisterView registerView;
    private MainApplication mainApp;
    
    public LoginController(MainApplication mainApp, DataManager dataManager) {
        this.mainApp = mainApp;
        this.dataManager = dataManager;
    }
    
    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }
    
    public void setRegisterView(RegisterView registerView) {
        this.registerView = registerView;
    }
    
    public void handleLogin(String username, String password, String userType) {
        // Add null check for loginView
        if (loginView == null) {
            System.err.println("Error: LoginView is not set in LoginController");
            return;
        }
        
        if ("Customer".equals(userType)) {
            Customer customer = dataManager.authenticateCustomer(username, password);
            if (customer != null) {
                loginView.showStatus("Login successful! Welcome " + customer.getName(), "success");
                
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            mainApp.onLoginSuccess(customer, null, username);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                loginView.showStatus("Invalid username or password", "error");
            }
        } else if ("Employee".equals(userType)) {
            Employee employee = dataManager.authenticateEmployee(username, password);
            if (employee != null) {
                loginView.showStatus("Login successful! Welcome " + employee.getName(), "success");
                
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            mainApp.onLoginSuccess(null, employee, username);
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
    
    public void handleRegister(String username, String password, String name, 
                               String userType, String employeeId) {
        if (registerView == null) {
            System.err.println("Error: RegisterView is not set in LoginController");
            return;
        }
        
        if (dataManager.usernameExists(username)) {
            registerView.showStatus("Username already exists. Please choose another.", "error");
            return;
        }
        
        boolean success = false;
        if ("Customer".equals(userType)) {
            success = dataManager.registerCustomer(username, password, name);
        } else if ("Employee".equals(userType)) {
            success = dataManager.registerEmployee(username, password, name, employeeId);
        }
        
        if (success) {
            registerView.showStatus("Account created successfully! Redirecting to login...", "success");
            
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
    
    public void handleShowLogin() {
        if (registerView != null) {
            registerView.clearFields();
        }
        mainApp.showLoginView();
    }
    
    public void handleShowRegister() {
        if (loginView != null) {
            loginView.clearFields();
        }
        mainApp.showRegisterView();
    }
}