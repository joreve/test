/******************************************************************************
 *  Description     : Main Application class for Convenience Store Management System.
 *  Author/s        : De Jesus, Joreve P., Pelagio, Dana Ysabelle A.
 *  Section         : S12
 *  Last Modified   : November 24, 2025
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
    private Scene loginScene;
    private Scene registerScene;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Convenience Store Management System");
        
        dataManager = new DataManager();
        initializeStore();
        initializeLoginSystem();
        showLoginView();
        
        primaryStage.setMaximized(true);
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
     */
    private void initializeLoginSystem() {
        loginController = new LoginController(this, dataManager);
        loginView = new LoginView(loginController);
        registerView = new RegisterView(loginController);
        
        loginController.setLoginView(loginView);
        loginController.setRegisterView(registerView);
        
        loginScene = new Scene(loginView, 800, 700);
        registerScene = new Scene(registerView, 800, 700);
    }
    
    
    /**
     * 
     */
    public void showLoginView() {
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Convenience Store");
    }
    
    /**
     * 
     */
    public void showRegisterView() {
        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Register - Convenience Store");
    }
    
    /**
     * @param customer
     * @param employee
     * @param username
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
     * 
     */
    private void showCustomerView() {
        ConvenienceStoreController storeController = new ConvenienceStoreController(
            primaryStage, store, currentCustomer, dataManager, currentUsername
        );
        storeController.showShoppingView();
    }
    

    /**
     * 
     */
    private void showEmployeeView() {
        reloadInventory();
        
        EmployeeView employeeView = new EmployeeView(store, currentEmployee, this, dataManager);
        Scene employeeScene = new Scene(employeeView);
        primaryStage.setScene(employeeScene);
        primaryStage.setTitle("Employee Dashboard - " + currentEmployee.getName());
    }
    
    /**
     * Reloads inventory from file to get latest stock levels.
     */
    private void reloadInventory() {
        store.getInventory().getProducts().clear();
        for (Shelf shelf : store.getInventory().getShelves()) {
            shelf.getProducts().clear();
        }
        
        for (Product product : dataManager.loadProducts()) {
            store.getInventory().addProduct(product);
            
            for (Shelf shelf : store.getInventory().getShelves()) {
                if (shelf.getCategory().getName().equals(product.getCategory().getName()) &&
                    shelf.getCategory().getType().equals(product.getCategory().getType())) {
                    shelf.addProduct(product);
                    break;
                }
            }
        }
    }
    
    /**
     * 
     */
    public void logout() {
        currentCustomer = null;
        currentEmployee = null;
        currentUsername = null;
        loginView.clearFields();
        registerView.clearFields();
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