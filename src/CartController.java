import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * CartController handles cart operations ONLY.
 * Manages quantity changes, item removal, and navigation to checkout.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class CartController {
    private Cart cart;
    private MainApplication mainApp;
    private CartView view;

    public CartController(Cart cart, MainApplication mainApp) {
        this.cart = cart;
        this.mainApp = mainApp;
    }
    
    public void setView(CartView view) {
        this.view = view;
    }

    public boolean isCartEmpty() {
        return cart.isEmpty();
    }

    public ArrayList<CartItem> getCartItems() {
        return cart.getItems();
    }

    public double computeSubtotal() {
        return cart.computeSubtotal();
    }

    /**
     * Handles quantity changes for a cart item.
     */
    public void handleQuantityChange(CartItem item, int newQuantity) {
        if (newQuantity <= 0) {
            handleRemoveItem(item);
            return;
        }

        if (newQuantity > item.getProduct().getStock()) {
            showAlert("Insufficient Stock",
                    "Only " + item.getProduct().getStock() + " units available.",
                    Alert.AlertType.WARNING);
            view.refreshCartDisplay();
            return;
        }

        item.setQuantity(newQuantity);
        view.refreshCartDisplay();
    }

    /**
     * Handles removing an item from the cart.
     */
    public void handleRemoveItem(CartItem item) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Remove Item");
        confirmAlert.setHeaderText("Remove from cart?");
        confirmAlert.setContentText("Remove " + item.getProduct().getName() + " from your cart?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cart.removeItem(item.getProduct());
                view.refreshCartDisplay();
            }
        });
    }

    /**
     * Handles clearing the entire cart.
     */
    public void handleClearCart() {
        if (cart.isEmpty()) {
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear Cart");
        confirmAlert.setHeaderText("Clear entire cart?");
        confirmAlert.setContentText("This will remove all items from your cart.");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cart.clear();
                view.refreshCartDisplay();
                showAlert("Cart Cleared", "All items removed from cart.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Handles proceeding to checkout.
     */
    public void handleCheckout() {
        if (cart.isEmpty()) {
            showAlert("Empty Cart", "Please add items to your cart first.", Alert.AlertType.WARNING);
            return;
        }

        mainApp.showCheckoutView();
    }
    
    /**
     * Handles going back to shopping.
     */
    public void handleBackToShopping() {
        mainApp.showCustomerView();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}