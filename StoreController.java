import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * StoreController manages the customer shopping experience.
 * Coordinates between product browsing, cart, and checkout views.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class StoreController {
    private Stage primaryStage;
    private ConvenienceStore store;
    private Customer customer;
    private DataPersistence dataPersistence;
    
    private Scene shoppingScene;
    
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
    }
    
    /**
     * Shows the main shopping view.
     */
    public void showShoppingView() {
        BorderPane mainLayout = new BorderPane();
        
        // Top bar
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);
        
        // Center: Product selection
        VBox centerPanel = createProductSelectionPanel();
        
        // Right: Cart view
        CartView cartView = new CartView(customer.getCart());
        
        // Override checkout button
        cartView.getCheckoutButton().setOnAction(e -> {
            if (customer.getCart().isEmpty()) {
                showAlert("Empty Cart", "Please add items to cart first.", Alert.AlertType.WARNING);
                return;
            }
            showCheckoutView(cartView);
        });
        
        // Split pane
        SplitPane splitPane = new SplitPane(centerPanel, cartView);
        splitPane.setDividerPositions(0.6);
        mainLayout.setCenter(splitPane);
        
        shoppingScene = new Scene(mainLayout, 1200, 700);
        primaryStage.setScene(shoppingScene);
        primaryStage.setTitle("Shopping - " + customer.getName());
    }
    
    /**
     * Creates the top bar with store and user info.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #4CAF50;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label storeLabel = new Label("🏪 " + store.getName() + " - " + store.getLocation());
        storeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        storeLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label customerLabel = new Label("👤 " + customer.getName());
        customerLabel.setFont(Font.font("Arial", 16));
        customerLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> handleLogout());
        
        topBar.getChildren().add(storeLabel);
        topBar.getChildren().add(spacer);
        topBar.getChildren().add(customerLabel);
        
        if (customer.hasMembershipCard()) {
            Label memberLabel = new Label("⭐ " + customer.getMembershipCard().getPoints() + " pts");
            memberLabel.setFont(Font.font("Arial", 14));
            memberLabel.setStyle("-fx-text-fill: #FFD700;");
            topBar.getChildren().add(memberLabel);
        }
        
        topBar.getChildren().add(logoutButton);
        
        return topBar;
    }
    
    /**
     * Creates the product selection panel.
     */
    private VBox createProductSelectionPanel() {
        VBox productPanel = new VBox(15);
        productPanel.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Available Products");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        GridPane productGrid = new GridPane();
        productGrid.setHgap(15);
        productGrid.setVgap(15);
        
        int row = 0, col = 0;
        for (Product product : store.getInventory().getProducts()) {
            VBox productBox = createProductBox(product);
            productGrid.add(productBox, col, row);
            
            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
        
        ScrollPane scrollPane = new ScrollPane(productGrid);
        scrollPane.setFitToWidth(true);
        
        productPanel.getChildren().addAll(titleLabel, scrollPane);
        return productPanel;
    }
    
    /**
     * Creates a product display box.
     */
    private VBox createProductBox(Product product) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; -fx-background-color: white;");
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(180);
        
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        
        Label priceLabel = new Label(String.format("₱%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", 16));
        priceLabel.setStyle("-fx-text-fill: #4CAF50;");
        
        Label stockLabel = new Label("Stock: " + product.getStock());
        stockLabel.setFont(Font.font("Arial", 12));
        
        Spinner<Integer> qtySpinner = new Spinner<>(1, product.getStock(), 1);
        qtySpinner.setPrefWidth(80);
        
        Button addButton = new Button("Add to Cart");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        addButton.setPrefWidth(150);
        
        addButton.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            customer.addToCart(product, qty);
            showAlert("Added to Cart", 
                     String.format("Added %dx %s", qty, product.getName()),
                     Alert.AlertType.INFORMATION);
            showShoppingView(); // Refresh
        });
        
        box.getChildren().addAll(nameLabel, priceLabel, stockLabel, qtySpinner, addButton);
        return box;
    }
    
    /**
     * Shows the checkout view.
     */
    private void showCheckoutView(CartView previousCartView) {
        CheckoutView checkoutView = new CheckoutView(customer, customer.getCart());
        
        checkoutView.getBackButton().setOnAction(e -> showShoppingView());
        
        checkoutView.getProcessPaymentButton().setOnAction(e -> {
            processCheckout(checkoutView);
        });
        
        Scene checkoutScene = new Scene(checkoutView, 1000, 700);
        primaryStage.setScene(checkoutScene);
        primaryStage.setTitle("Checkout - " + customer.getName());
    }
    
    /**
     * Processes checkout and shows receipt.
     */
    private void processCheckout(CheckoutView checkoutView) {
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
            
            // Save customer data
            // Note: You'll need to track username separately or modify Customer class
            
            // Generate receipt
            Receipt receipt = transaction.generateReceipt();
            ReceiptView receiptView = new ReceiptView(receipt);
            receiptView.show();
            
            showAlert("Transaction Complete",
                     String.format("Change: ₱%.2f\nThank you for shopping!", payment.computeChange()),
                     Alert.AlertType.INFORMATION);
            
            // Return to shopping
            showShoppingView();
            
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid amount.", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Handles logout.
     */
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Logout Confirmation");
        confirm.setContentText("Are you sure you want to logout?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Return to login through MainApplication
                // This requires MainApplication reference
                loginView();
            }
        });
    }
    
    /**
     * Returns to login view.
     */
    private void loginView() {
        // Recreate login view
        LoginController loginController = new LoginController(null);
        LoginView loginView = new LoginView(loginController);
        
        Scene loginScene = new Scene(loginView, 800, 700);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Login - Convenience Store");
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