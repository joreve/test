/******************************************************************************
 *  Description     : Main Application class for Convenience Store Management System.
 *                    Centralized navigation and controller creation.
 *  Author/s        : De Jesus, Joreve P., Pelagio, Dana Ysabelle A.
 *  Section         : S12
 *  Last Modified   : November 26, 2025
 ******************************************************************************/
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * MainApplication serves as the entry point and navigation coordinator.
 * Creates all controllers and views, wires them together.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class MainApplication extends Application {
    
    private Stage primaryStage;
    private DataManager dataManager;
    private ConvenienceStore store;
    
    // Current user
    private Customer currentCustomer;
    private Employee currentEmployee;
    
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

        // Initialize data and store
        dataManager = new DataManager();
        initializeStore();
        
        // Show login
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
    
    // ==================== NAVIGATION METHODS ====================
    
    /**
     * Shows the login screen.
     */
    public void showLoginView() {
        // Create controller with dependencies
        AuthenticationController authController = new AuthenticationController(dataManager, this);
        
        // Create view
        LoginView loginView = new LoginView();
        
        // Connect them
        loginView.setController(authController);
        authController.setView(loginView);
        
        // Display
        Scene scene = new Scene(loginView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login - Convenience Store");
    }
    
    /**
     * Shows the registration screen.
     */
    public void showRegisterView() {
        // Create controller
        AuthenticationController authController = new AuthenticationController(dataManager, this);
        
        // Create view
        RegisterView registerView = new RegisterView();
        
        // Connect them
        registerView.setController(authController);
        authController.setRegisterView(registerView);
        
        // Display
        Scene scene = new Scene(registerView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register - Convenience Store");
    }
    
    /**
     * Called after successful login. Routes to appropriate dashboard.
     *
     * @param customer the logged-in customer (null if employee)
     * @param employee the logged-in employee (null if customer)
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
    public void showCustomerView() {
        reloadInventory();
        
        // Create controller with dependencies
        ShoppingController shoppingController = new ShoppingController(
            currentCustomer, 
            this  // Pass MainApplication directly
        );
        
        // Create view
        CustomerView customerView = new CustomerView(store, currentCustomer);
        
        // Connect them
        customerView.setController(shoppingController);
        shoppingController.setView(customerView);
        
        // Display
        Scene scene = new Scene(customerView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("11-Seven - Shopping");
    }
    
    /**
     * Shows the shopping cart view.
     */
    public void showCartView() {
        if (currentCustomer.getCart().isEmpty()) {
            // Could show alert here, but let controller handle it
            return;
        }
        
        // Create controller
        CartController cartController = new CartController(
            currentCustomer.getCart(),
            this
        );
        
        // Create view
        CartView cartView = new CartView(currentCustomer.getCart());
        
        // Connect them
        cartView.setController(cartController);
        cartController.setView(cartView);
        
        // Display
        Scene scene = new Scene(cartView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Shopping Cart");
    }
    
    /**
     * Shows the checkout view.
     */
    public void showCheckoutView() {
        // Create controller
        CheckoutController checkoutController = new CheckoutController(
            currentCustomer,
            currentCustomer.getCart(),
            store,
            dataManager,
            this
        );
        
        // Create view
        CheckoutView checkoutView = new CheckoutView(currentCustomer, currentCustomer.getCart());
        
        // Connect them
        checkoutView.setController(checkoutController);
        checkoutController.setView(checkoutView);
        
        // Display
        Scene scene = new Scene(checkoutView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Checkout");
    }
    
    /**
     * Shows the employee dashboard view.
     */
    public void showEmployeeView() {
        reloadInventory();
        
        // Create controller
        EmployeeController employeeController = new EmployeeController(
            store,
            currentEmployee,
            dataManager,
            this
        );
        
        // Create view
        EmployeeView employeeView = new EmployeeView(store, currentEmployee);
        
        // Connect them
        employeeView.setController(employeeController);
        employeeController.setView(employeeView);
        
        // Display
        Scene scene = new Scene(employeeView);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Employee Dashboard - " + currentEmployee.getName());
    }
    
    /**
     * Logs out current user and returns to login screen.
     */
    public void logout() {
        currentCustomer = null;
        currentEmployee = null;
        showLoginView();
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