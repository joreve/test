import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * StoreController manages the customer shopping experience.
 * Coordinates between product browsing, cart, and checkout views.
 * Updated to use ConvenienceStoreView with navigation.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class StoreController {
    private Stage primaryStage;
    private ConvenienceStore store;
    private Customer customer;
    private DataPersistence dataPersistence;
    private ProductDataManager productDataManager;
    
    private Scene storeScene;
    private Scene cartScene;
    private Scene checkoutScene;
    
    private ConvenienceStoreView storeView;
    private CartView cartView;
    private CheckoutView checkoutView;
    
    /**
     * Constructs a StoreController.
     *
     * @param primaryStage the primary stage
     * @param store the convenience store
     * @param customer the current customer
     * @param dataPersistence data persistence handler
     */
    public StoreController(Stage primaryStage, ConvenienceStore store, 
                          Customer customer, DataPersistence dataPersistence) {
        this.primaryStage = primaryStage;
        this.store = store;
        this.customer = customer;
        this.dataPersistence = dataPersistence;
        this.productDataManager = new ProductDataManager();
        
        // Set stage to maximized
        primaryStage.setMaximized(true);
    }
    
    /**
     * Shows the main shopping view (ConvenienceStoreView).
     */
    public void showShoppingView() {
        storeView = new ConvenienceStoreView(store, customer, this);
        storeScene = new Scene(storeView);
        
        primaryStage.setScene(storeScene);
        primaryStage.setTitle("11-Seven - Shopping");
    }
    
    /**
     * Shows the cart view.
     */
    public void showCartView() {
        if (customer.getCart().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some items first!", Alert.AlertType.WARNING);
            return;
        }
        
        cartView = new CartView(customer.getCart());
        
        // Override checkout button
        cartView.getCheckoutButton().setOnAction(e -> {
            showCheckoutView();
        });
        
        // Add back button to cart view
        Button backButton = new Button("← Back to Store");
        backButton.setStyle("-fx-font-size: 14px;");
        backButton.setOnAction(e -> {
            showShoppingView();
            storeView.updateCartCount();
        });
        
        BorderPane cartLayout = new BorderPane();
        cartLayout.setCenter(cartView);
        
        HBox bottomBox = new HBox(backButton);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        cartLayout.setBottom(bottomBox);
        
        cartScene = new Scene(cartLayout);
        primaryStage.setScene(cartScene);
        primaryStage.setTitle("Shopping Cart");
    }
    
    /**
     * Shows the checkout view.
     */
    public void showCheckoutView() {
        checkoutView = new CheckoutView(customer, customer.getCart());
        
        checkoutView.getBackButton().setOnAction(e -> showCartView());
        
        checkoutView.getProcessPaymentButton().setOnAction(e -> {
            processCheckout();
        });
        
        checkoutScene = new Scene(checkoutView);
        primaryStage.setScene(checkoutScene);
        primaryStage.setTitle("Checkout");
    }
    
    /**
     * Processes checkout and shows receipt.
     */
    private void processCheckout() {
        try {
            String amountText = checkoutView.getAmountReceived();
            if (amountText == null || amountText.trim().isEmpty()) {
                showAlert("Invalid Payment", "Please enter payment amount.", Alert.AlertType.WARNING);
                return;
            }
            
            double amountReceived = Double.parseDouble(amountText);
            double subtotal = customer.getCart().computeSubtotal();
            double afterDiscount = subtotal;
            
            if (checkoutView.isSeniorDiscount()) {
                afterDiscount = DiscountPolicy.applySeniorDiscount(afterDiscount);
            }
            
            if (checkoutView.isUseMembershipPoints() && customer.hasMembershipCard()) {
                afterDiscount = DiscountPolicy.applyMembershipDiscount(customer, afterDiscount);
            }
            
            double vat = DiscountPolicy.calculateVAT(afterDiscount);
            double total = afterDiscount + vat;
            
            if (amountReceived < total) {
                showAlert("Insufficient Payment", 
                         String.format("Need ₱%.2f more", total - amountReceived),
                         Alert.AlertType.WARNING);
                return;
            }
            
            // Create transaction
            Payment payment = new Payment(amountReceived, total);
            Transaction transaction = customer.checkOut(store);
            transaction.setPayment(payment);
            
            // Update membership points
            if (customer.hasMembershipCard()) {
                MembershipCard card = customer.getMembershipCard();
                if (checkoutView.isUseMembershipPoints()) {
                    int pointsToRedeem = Math.min(card.getPoints(), (int)subtotal);
                    card.redeemPoints(pointsToRedeem);
                }
                card.addPoints(total);
            }
            
            // Update product stock in file
            productDataManager.saveProducts(store.getInventory().getProducts());
            
            // Generate receipt
            Receipt receipt = transaction.generateReceipt();
            ReceiptView receiptView = new ReceiptView(receipt);
            receiptView.show();
            
            showAlert("Transaction Complete",
                     String.format("Change: ₱%.2f\nThank you for shopping!", payment.computeChange()),
                     Alert.AlertType.INFORMATION);
            
            // Return to store view
            showShoppingView();
            storeView.refresh();
            
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid amount.", Alert.AlertType.ERROR);
        }
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
                // Clear cart and return to login
                customer.getCart().clear();
                
                // Navigate back to login
                LoginController loginController = new LoginController(null);
                LoginView loginView = new LoginView(loginController);
                
                Scene loginScene = new Scene(loginView);
                primaryStage.setScene(loginScene);
                primaryStage.setTitle("Login - Convenience Store");
                primaryStage.setMaximized(false);
                primaryStage.setWidth(800);
                primaryStage.setHeight(700);
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
}