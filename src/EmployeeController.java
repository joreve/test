import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.Optional;

/**
 * EmployeeController manages employee interactions with the inventory system.
 * Handles product management, restocking, and sales history operations.
 *
 * @author Joreve P. De Jesus
 */
public class EmployeeController {
    private Stage primaryStage;
    private ConvenienceStore store;
    private Employee employee;
    private MainApplication mainApp;
    private DataManager dataManager;
    private EmployeeView employeeView;
    
    public EmployeeController(Stage primaryStage, ConvenienceStore store, 
                             Employee employee, MainApplication mainApp, DataManager dataManager) {
        this.primaryStage = primaryStage;
        this.store = store;
        this.employee = employee;
        this.mainApp = mainApp;
        this.dataManager = dataManager;
    }
    
    /**
     * Shows the employee dashboard view.
     */
    public void showEmployeeView() {
        employeeView = new EmployeeView(store, employee, this);
        Scene employeeScene = new Scene(employeeView);
        primaryStage.setScene(employeeScene);
        primaryStage.setTitle("Employee Dashboard - " + employee.getName());
    }
    
    /**
     * Handles restocking a product.
     */
    public void handleRestock(Product product, int quantity) {
        employee.restockItem(store.getInventory(), product, quantity);
        dataManager.saveProducts(store.getInventory().getProducts());
        employeeView.refreshInventory();
        showAlert("Success", "Product restocked successfully!", Alert.AlertType.INFORMATION);
    }
    
    /**
     * Handles editing product information.
     */
    public void handleEditProduct(Product updatedProduct) {
        employee.updateProductInfo(store.getInventory(), updatedProduct);
        dataManager.saveProducts(store.getInventory().getProducts());
        employeeView.refreshInventory();
        showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
    }
    
    /**
     * Handles removing a product from inventory.
     */
    public void handleRemoveProduct(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Product");
        confirm.setHeaderText("Remove: " + product.getName());
        confirm.setContentText("Are you sure you want to remove this product from inventory?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                store.getInventory().removeProduct(product.getProductID());
                dataManager.saveProducts(store.getInventory().getProducts());
                employeeView.refreshInventory();
                showAlert("Success", "Product removed successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    /**
     * Handles adding a new product to inventory.
     */
    public void handleAddProduct(int id, String name, double price, int stock, 
                                String mainCategory, String subCategory, 
                                String brand, String variant, LocalDate expDate) {
        if (store.getInventory().productExists(id)) {
            showAlert("Error", "Product ID already exists!", Alert.AlertType.ERROR);
            return;
        }
        
        Category category = new Category(mainCategory, subCategory);
        Product product = new Product(id, name, price, stock, category, brand, variant, expDate);
        
        employee.addProduct(store.getInventory(), product);
        dataManager.saveProducts(store.getInventory().getProducts());
        
        // Add product to appropriate shelf
        boolean shelfFound = false;
        for (Shelf shelf : store.getInventory().getShelves()) {
            if (shelf.getCategory().getName().equals(category.getName()) &&
                shelf.getCategory().getType().equals(category.getType())) {
                shelf.addProduct(product);
                shelfFound = true;
                break;
            }
        }
        
        // Create new shelf if category doesn't exist
        if (!shelfFound) {
            Shelf newShelf = new Shelf(category);
            newShelf.addProduct(product);
            store.getInventory().addShelf(newShelf);
        }
        
        employeeView.refreshInventory();
        showAlert("Success", "Product added successfully!", Alert.AlertType.INFORMATION);
    }
    
    /**
     * Handles employee logout.
     */
    public void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Logout Confirmation");
        confirm.setContentText("Are you sure you want to logout?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                mainApp.logout();
            }
        });
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
    
    public DataManager getDataManager() {
        return dataManager;
    }
}