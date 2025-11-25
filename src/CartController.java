import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * CartController handles user interactions with the shopping cart.
 * Manages adding, removing, and updating cart items.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class CartController {
    private Cart cart;
    private CartView cartView;

    /**
     * Constructs a CartController for the specified cart and view.
     *
     * @param cart the cart model
     * @param cartView the cart view
     */
    public CartController(Cart cart, CartView cartView) {
        this.cart = cart;
        this.cartView = cartView;
    }

    /**
     * Handles quantity changes for a cart item.
     *
     * @param item the cart item to update
     * @param newQuantity the new quantity
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
            cartView.refreshCartDisplay();
            return;
        }

        item.setQuantity(newQuantity);
        cartView.refreshCartDisplay();
    }

    /**
     * Handles removing an item from the cart.
     *
     * @param item the cart item to remove
     */
    public void handleRemoveItem(CartItem item) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Remove Item");
        confirmAlert.setHeaderText("Remove from cart?");
        confirmAlert.setContentText("Remove " + item.getProduct().getName() + " from your cart?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cart.removeItem(item.getProduct());
                cartView.refreshCartDisplay();
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
                cartView.refreshCartDisplay();
                showAlert("Cart Cleared", "All items removed from cart.", Alert.AlertType.INFORMATION);
            }
        });
    }

    /**
     * Handles proceeding to checkout.
     * This should be connected to CheckoutView by the main controller.
     */
    public void handleCheckout() {
        if (cart.isEmpty()) {
            showAlert("Empty Cart", "Please add items to your cart first.", Alert.AlertType.WARNING);
            return;
        }

        // This will be handled by the main StoreController
        // which will switch to CheckoutView
        System.out.println("Proceeding to checkout with " + cart.getItems().size() + " items");
    }

    /**
     * Shows an alert dialog with the specified message.
     *
     * @param title the alert title
     * @param message the alert message
     * @param type the alert type
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}