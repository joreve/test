import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * ShoppingController handles product browsing and adding items to cart.
 * This is the ONLY controller for the shopping screen.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class ShoppingController {
    private Customer customer;
    // private ConvenienceStore store;
    // private DataManager dataManager;
    private MainApplication mainApp;
    
    private CustomerView view;
    
    public ShoppingController(Customer customer, MainApplication mainApp) {
        this.customer = customer;
        // this.store = store;
        // this.dataManager = dataManager;
        this.mainApp = mainApp;
    }
    
    public void setView(CustomerView view) {
        this.view = view;
    }
    
    /**
     * Handles adding a product to cart.
     */
    public void handleAddToCart(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }
        
        if (quantity > product.getStock()) {
            showAlert("Insufficient Stock", 
                     "Only " + product.getStock() + " units available.", 
                     Alert.AlertType.WARNING);
            return;
        }
        
        customer.addToCart(product, quantity);
        view.updateCartCount();
        view.showNotification("Added " + quantity + "x " + product.getName() + " to cart");
    }
    
    /**
     * Handles navigating to cart view.
     */
    public void handleViewCart() {
        if (customer.getCart().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some items first!", Alert.AlertType.WARNING);
            return;
        }
        
        mainApp.showCartView();
    }
    
    /**
     * Handles logout.
     */
    public void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Logout Confirmation");
        confirm.setContentText("Are you sure you want to logout?\nYour cart will be cleared.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                mainApp.logout();
            }
        });
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}