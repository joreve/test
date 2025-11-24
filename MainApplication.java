/******************************************************************************
 *  Description     : Main Application class for Convenience Store Management System.
 *                    Single entry point following MVC architecture with login system.
 *  Author/s        : De Jesus, Joreve P., Pelagio, Dana Ysabelle A.
 *  Section         : S12
 *  Last Modified   : November 24, 2025
 ******************************************************************************/
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.time.LocalDate;

/**
 * MainApplication serves as the single entry point for the application.
 * Manages navigation between login, registration, and main application views.
 * Follows MVC architecture pattern.
 *
 * @author Dana Ysabelle A. Pelagio, Joreve P. De Jesus
 */
public class MainApplication extends Application {
    
    // Application window
    private Stage primaryStage;
    
    // Store and user data
    private ConvenienceStore store;
    private Customer currentCustomer;
    private Employee currentEmployee;
    
    // Controllers
    private LoginController loginController;
    
    // Views
    private LoginView loginView;
    private RegisterView registerView;
    private Scene loginScene;
    private Scene registerScene;
    
    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Starts the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Convenience Store Management System");
        
        // Initialize store with sample data
        initializeStore();
        
        // Initialize login system
        initializeLoginSystem();
        
        // Show login view
        showLoginView();
        
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        primaryStage.show();
    }
    
    /**
     * Initializes the store with sample products.
     */
    private void initializeStore() {
        store = new ConvenienceStore("11-Seven", "Taft");
        
        // Create categories
        Category snacks = new Category("Food", "Snacks");
        Category beverages = new Category("Beverages", "Cold Drinks");
        Category vegetables = new Category("Food", "Vegetables");
        Category fruits = new Category("Food", "Fruits");
        
        // Create shelves
        Shelf snacksShelf = new Shelf(snacks);
        Shelf beveragesShelf = new Shelf(beverages);
        Shelf vegetablesShelf = new Shelf(vegetables);
        Shelf fruitsShelf = new Shelf(fruits);
        
        store.getInventory().addShelf(snacksShelf);
        store.getInventory().addShelf(beveragesShelf);
        store.getInventory().addShelf(vegetablesShelf);
        store.getInventory().addShelf(fruitsShelf);
        
        // Create products
        Product chips = new Product(301, "Potato Chips", 35.00, 30, snacks, 
                                   "Lay's", "Classic", null);
        Product cookies = new Product(302, "Cookies", 35.00, 25, snacks, 
                                     "Oreo", "Original", null);
        Product soda = new Product(401, "Soda", 20.00, 40, beverages, 
                                  "Coke", "Regular", null);
        Product juice = new Product(402, "Orange Juice", 45.00, 35, beverages, 
                                   "Minute Maid", "1L", null);
        Product carrot = new Product(101, "Carrot", 20.00, 30, vegetables);
        Product apple = new Product(201, "Apple", 50.00, 40, fruits);
        Product banana = new Product(202, "Banana", 40.00, 50, fruits, 
                                    null, null, LocalDate.of(2025, 11, 30));
        Product broccoli = new Product(102, "Broccoli", 60.00, 20, vegetables, 
                                      null, null, LocalDate.of(2025, 11, 28));
        
        // Add products to inventory
        Employee storeManager = new Employee("Store Manager", "EMP000");
        storeManager.addProduct(store.getInventory(), chips);
        storeManager.addProduct(store.getInventory(), cookies);
        storeManager.addProduct(store.getInventory(), soda);
        storeManager.addProduct(store.getInventory(), juice);
        storeManager.addProduct(store.getInventory(), carrot);
        storeManager.addProduct(store.getInventory(), apple);
        storeManager.addProduct(store.getInventory(), banana);
        storeManager.addProduct(store.getInventory(), broccoli);
        
        // Add products to shelves
        snacksShelf.addProduct(chips);
        snacksShelf.addProduct(cookies);
        beveragesShelf.addProduct(soda);
        beveragesShelf.addProduct(juice);
        vegetablesShelf.addProduct(carrot);
        vegetablesShelf.addProduct(broccoli);
        fruitsShelf.addProduct(apple);
        fruitsShelf.addProduct(banana);
        
        System.out.println("Store initialized with sample products.");
    }
    
    /**
     * Initializes the login system.
     */
    private void initializeLoginSystem() {
        loginController = new LoginController(this);
        
        loginView = new LoginView(loginController);
        registerView = new RegisterView(loginController);
        
        loginController.setLoginView(loginView);
        loginController.setRegisterView(registerView);
        
        loginScene = new Scene(loginView, 800, 700);
        registerScene = new Scene(registerView, 800, 700);
    }
    
    /**
     * Shows the login view.
     */
    public void showLoginView() {
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Convenience Store");
    }
    
    /**
     * Shows the register view.
     */
    public void showRegisterView() {
        primaryStage.setScene(registerScene);
        primaryStage.setTitle("Register - Convenience Store");
    }
    
    /**
     * Called when login is successful.
     *
     * @param customer the logged in customer (null if employee)
     * @param employee the logged in employee (null if customer)
     */
    public void onLoginSuccess(Customer customer, Employee employee) {
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
        // Use existing MainGuiApp logic but adapted
        StoreController storeController = new StoreController(
            primaryStage, store, currentCustomer, loginController.getDataPersistence()
        );
        storeController.showShoppingView();
    }
    
    /**
     * Shows the employee management view.
     */
    private void showEmployeeView() {
        EmployeeView employeeView = new EmployeeView(store, currentEmployee, this);
        Scene employeeScene = new Scene(employeeView, 1000, 700);
        primaryStage.setScene(employeeScene);
        primaryStage.setTitle("Employee Dashboard - " + currentEmployee.getName());
    }
    
    /**
     * Logs out the current user and returns to login.
     */
    public void logout() {
        currentCustomer = null;
        currentEmployee = null;
        loginView.clearFields();
        showLoginView();
    }
    
    /**
     * Gets the primary stage.
     *
     * @return the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Gets the store.
     *
     * @return the convenience store
     */
    public ConvenienceStore getStore() {
        return store;
    }
}

/**
 * This is to certify that this project is my/our own work, based on my/our personal
 * efforts in studying and applying the concepts learned. I/We have constructed the
 * functions and their respective algorithms and corresponding code by myself/ourselves.
 * The program was run, tested, and debugged by my/our own efforts. I/We further certify
 * that I/we have not copied in part or whole or otherwise plagiarized the work of
 * other students and/or persons.
 *
 * (De Jesus, Joreve P.) (DLSU ID# 12410179)
 * (Pelagio, Dana Ysabelle A.) (DLSU ID# 12214973)
 */