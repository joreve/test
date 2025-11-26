import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.time.LocalDate;

/**
 * EmployeeController manages inventory operations.
 * Handles product management, restocking, and sales history.
 *
 * @author Joreve P. De Jesus
 */
public class EmployeeController {
    private ConvenienceStore store;
    private Employee employee;
    private DataManager dataManager;
    private MainApplication mainApp;
    private EmployeeView view;
    
    public EmployeeController(ConvenienceStore store, Employee employee,
                             DataManager dataManager, MainApplication mainApp) {
        this.store = store;
        this.employee = employee;
        this.dataManager = dataManager;
        this.mainApp = mainApp;
    }
    
    public void setView(EmployeeView view) {
        this.view = view;
    }
    
    /**
     * Handles restocking a product.
     */
    public void handleRestock(Product product, int quantity) {
        if (quantity <= 0) {
            showAlert("Invalid Quantity", "Please enter a positive quantity.", Alert.AlertType.WARNING);
            return;
        }
        
        employee.restockItem(store.getInventory(), product, quantity);
        dataManager.saveProducts(store.getInventory().getProducts());
        view.refreshInventory();
        showAlert("Success", "Product restocked successfully!", Alert.AlertType.INFORMATION);
    }
    
    /**
     * Handles editing product information.
     */
    public void handleEditProduct(Product updatedProduct) {
        employee.updateProductInfo(store.getInventory(), updatedProduct);
        dataManager.saveProducts(store.getInventory().getProducts());
        view.refreshInventory();
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
                view.refreshInventory();
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
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            showAlert("Error", "Product name is required.", Alert.AlertType.ERROR);
            return;
        }
        
        if (price < 0) {
            showAlert("Error", "Price cannot be negative.", Alert.AlertType.ERROR);
            return;
        }
        
        if (stock < 0) {
            showAlert("Error", "Stock cannot be negative.", Alert.AlertType.ERROR);
            return;
        }
        
        if (store.getInventory().productExists(id)) {
            showAlert("Error", "Product ID already exists!", Alert.AlertType.ERROR);
            return;
        }
        
        // Create product
        Category category = new Category(mainCategory, subCategory);
        Product product = new Product(id, name, price, stock, category, brand, variant, expDate);
        
        // Add to inventory
        employee.addProduct(store.getInventory(), product);
        dataManager.saveProducts(store.getInventory().getProducts());
        
        // Add to appropriate shelf
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
        
        view.refreshInventory();
        view.showAddProductSuccess();
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
     * Gets the DataManager for sales history loading.
     */
    public DataManager getDataManager() {
        return dataManager;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}