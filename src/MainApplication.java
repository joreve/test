/******************************************************************************
 *  Description     : Main Application class for Convenience Store Management System.
 *  Author/s        : De Jesus, Joreve P., Pelagio, Dana Ysabelle A.
 *  Section         : S12
 *  Last Modified   : November 25, 2025
 ******************************************************************************/
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApplication serves as the single entry point for the application.
 * Manages navigation between login, registration, and main application views.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class MainApplication extends Application {
    
    private Stage primaryStage;
    private ConvenienceStore store;
    private Customer currentCustomer;
    private Employee currentEmployee;
    private String currentUsername;
    
    private LoginController loginController;
    private DataManager dataManager;
    
    private LoginView loginView;
    private RegisterView registerView;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Convenience Store Management System");
        
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(true);

        dataManager = new DataManager();
        initializeStore();
        initializeLoginSystem();
        
        showLoginView();

        primaryStage.show();
    }
    
    /**
     * Initializes the store with products loaded from file.
     */
    private void initializeStore() {
        store = new ConvenienceStore("11-Seven", "Taft");

        Inventory inventory = store.getInventory();

        List<Product> loadedProducts = dataManager.loadProducts();

        Map<String, Shelf> shelfMap = new HashMap<>();

        for (Product product : loadedProducts) {
            store.getInventory().addProduct(product);

            String key = product.getCategory().getName() + "-" + product.getCategory().getType();

            if (!shelfMap.containsKey(key)) {
                Shelf newShelf = new Shelf(product.getCategory());
                shelfMap.put(key, newShelf);
                inventory.addShelf(newShelf);
            }

            shelfMap.get(key).addProduct(product);
        }
    }

    /**
     * Sets up the login and registration views and their controller.
     * This method is called to recreate the login system after logout.
     */
    private void initializeLoginSystem() {
        loginController = new LoginController(this, dataManager);
        loginView = new LoginView(loginController);
        registerView = new RegisterView(loginController);
        
        loginController.setLoginView(loginView);
        loginController.setRegisterView(registerView);
    }
    
    /**
     * Shows the login view.
     */
    public void showLoginView() {
        loginView = new LoginView(loginController);
        Scene loginScene = new Scene(loginView);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Convenience Store");
    }
    
    /**
     * Shows the registration view.
     */
    public void showRegisterView() {
        Scene registerScene = new Scene(registerView);

        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Register - Convenience Store");
    }
    
    /**
     * Called when login is successful. Routes to appropriate view.
     * 
     * @param customer the logged-in customer (null if employee)
     * @param employee the logged-in employee (null if customer)
     * @param username the username used to login
     */
    public void onLoginSuccess(Customer customer, Employee employee, String username) {
        this.currentUsername = username;
        
        if (customer != null) {
            this.currentCustomer = customer;
            this.currentEmployee = null;
            showCustomerView();
        } else if (employee != null) {
            this.currentCustomer = null;
            this.currentEmployee = employee;
            showEmployeeView();
        }
    }
    
    /**
     * Shows the customer shopping view.
     */
    private void showCustomerView() {
        ConvenienceStoreController storeController = new ConvenienceStoreController(
            primaryStage, store, currentCustomer, this, dataManager, currentUsername
        );
        storeController.showShoppingView();
    }
    
    /**
     * Shows the employee dashboard view.
     */
    private void showEmployeeView() {
        reloadInventory();
        
        EmployeeController employeeController = new EmployeeController(
            primaryStage, store, currentEmployee, this, dataManager
        );
        employeeController.showEmployeeView();
    }
    
    /**
     * Reloads inventory from file to get latest stock levels.
     */
    private void reloadInventory() {
        store.getInventory().getProducts().clear();
        for (Shelf shelf : store.getInventory().getShelves()) {
            shelf.getProducts().clear();
        }
        
        List<Product> loadedProducts = dataManager.loadProducts();
        Map<String, Shelf> shelfMap = new HashMap<>();
        
        for (Shelf shelf : store.getInventory().getShelves()) {
            String key = shelf.getCategory().getName() + "-" + shelf.getCategory().getType();
            shelfMap.put(key, shelf);
        }
        
        for (Product product : loadedProducts) {
            store.getInventory().addProduct(product);
            
            String key = product.getCategory().getName() + "-" + product.getCategory().getType();
            
            if (!shelfMap.containsKey(key)) {
                Shelf newShelf = new Shelf(product.getCategory());
                shelfMap.put(key, newShelf);
                store.getInventory().addShelf(newShelf);
            }
            
            shelfMap.get(key).addProduct(product);
        }
    }
    
    /**
     * Logs out the current user and returns to login screen.
     */
    public void logout() {
        currentCustomer = null;
        currentEmployee = null;
        currentUsername = null;
        
        // Reinitialize login system to ensure clean state
        initializeLoginSystem();
        showLoginView();
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public ConvenienceStore getStore() {
        return store;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
}