import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * ConvenienceStoreController manages the customer shopping experience.
 * Updated to properly work with Customer objects.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class ConvenienceStoreController {
    private Stage primaryStage;
    private ConvenienceStore store;
    private Customer customer;
    private MainApplication mainApp;
    private DataManager dataManager;
    
    private Scene storeScene;
    private Scene cartScene;
    private Scene checkoutScene;
    
    private ConvenienceStoreView storeView;
    private CartView cartView;
    private CheckoutView checkoutView;
    
    public ConvenienceStoreController(Stage primaryStage, ConvenienceStore store, 
                          Customer customer, MainApplication mainApp, DataManager dataManager) {
        this.primaryStage = primaryStage;
        this.store = store;
        this.customer = customer;
        this.mainApp = mainApp;
        this.dataManager = dataManager;
    }
    
    public void showShoppingView() {
        storeView = new ConvenienceStoreView(store, customer, this);
        storeScene = new Scene(storeView);
        
        primaryStage.setScene(storeScene);
        primaryStage.setTitle("11-Seven - Shopping");
    }
    
    public void showCartView() {
        if (customer.getCart().isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some items first!", Alert.AlertType.WARNING);
            return;
        }
        
        cartView = new CartView(customer.getCart());
        cartView.getCheckoutButton().setOnAction(e -> showCheckoutView());
        
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
    
    public void showCheckoutView() {
        checkoutView = new CheckoutView(customer, customer.getCart());
        checkoutView.getBackButton().setOnAction(e -> showCartView());
        checkoutView.getProcessPaymentButton().setOnAction(e -> processCheckout());
        
        checkoutScene = new Scene(checkoutView);
        
        primaryStage.setScene(checkoutScene);
        primaryStage.setTitle("Checkout");
    }
    
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
            
            Payment payment = new Payment(amountReceived, total);
            Transaction transaction = customer.checkOut(store);
            transaction.setPayment(payment);
            
            if (customer.hasMembershipCard()) {
                MembershipCard card = customer.getMembershipCard();
                if (checkoutView.isUseMembershipPoints()) {
                    int pointsToRedeem = Math.min(card.getPoints(), (int)subtotal);
                    card.redeemPoints(pointsToRedeem);
                }
                card.addPoints(total);
                
                // Update customer in file
                dataManager.updateCustomer(customer);
            }
            
            dataManager.saveProducts(store.getInventory().getProducts());
            dataManager.saveTransaction(transaction);
            
            // Generate and save receipt automatically
            Receipt receipt = transaction.generateReceipt();
            receipt.setDataManager(dataManager);
            receipt.saveToFile(); // Auto-save receipt
            
            ReceiptView receiptView = new ReceiptView(receipt);
            receiptView.show();
            
            showAlert("Transaction Complete",
                     String.format("Change: ₱%.2f\nReceipt saved automatically.\nThank you for shopping!", 
                                 payment.computeChange()),
                     Alert.AlertType.INFORMATION);
            
            // Reload inventory to reflect updated stock
            reloadInventory();
            showShoppingView();
            storeView.refresh();
            
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid amount.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Reloads inventory from data manager to get latest stock levels.
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