import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main GUI Application for the Convenience Store Management System.
 * Launches the JavaFX interface and coordinates between different views.
 *
 */
public class MainGuiApp extends Application {

    private Stage primaryStage;
    private ConvenienceStore store;
    private Customer currentCustomer;
    private Scene mainScene;

    /**
     * Main entry point for the application.
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

        // Initialize store and data
        initializeStoreData();

        // Show welcome screen
        showWelcomeScreen();

        primaryStage.show();
    }

    /**
     * Initializes the store with sample data.
     */
    private void initializeStoreData() {
        // Create store
        store = new ConvenienceStore("11-Seven", "Taft");

        // Create categories
        Category snacks = new Category("Food", "Snacks");
        Category beverages = new Category("Beverages", "Cold Drinks");

        // Create shelves
        Shelf snacksShelf = new Shelf(snacks);
        Shelf beveragesShelf = new Shelf(beverages);

        store.getInventory().addShelf(snacksShelf);
        store.getInventory().addShelf(beveragesShelf);

        // Create products
        Product chips = new Product(301, "Potato Chips", 35.00, 30, snacks, "Lay's", "Classic", null);
        Product cookies = new Product(302, "Cookies", 35.00, 25, snacks, "Oreo", "Original", null);
        Product soda = new Product(401, "Soda", 20.00, 40, beverages, "Coke", "Regular", null);
        Product juice = new Product(402, "Orange Juice", 45.00, 35, beverages, "Minute Maid", "1L", null);

        // Add products to inventory
        Employee employee = new Employee("Store Manager", "EMP001");
        employee.addProduct(store.getInventory(), chips);
        employee.addProduct(store.getInventory(), cookies);
        employee.addProduct(store.getInventory(), soda);
        employee.addProduct(store.getInventory(), juice);

        // Add products to shelves
        snacksShelf.addProduct(chips);
        snacksShelf.addProduct(cookies);
        beveragesShelf.addProduct(soda);
        beveragesShelf.addProduct(juice);

        System.out.println("Store initialized with sample products.");
    }

    /**
     * Shows the welcome screen with customer name input.
     */
    private void showWelcomeScreen() {
        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setPadding(new Insets(50));
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Title
        Label titleLabel = new Label("Welcome to 11-Seven");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setStyle("-fx-text-fill: white;");

        Label subtitleLabel = new Label("Convenience Store Management System");
        subtitleLabel.setFont(Font.font("Arial", 18));
        subtitleLabel.setStyle("-fx-text-fill: #f0f0f0;");

        // Customer name input
        Label nameLabel = new Label("Enter your name:");
        nameLabel.setFont(Font.font("Arial", 16));
        nameLabel.setStyle("-fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setPromptText("Customer Name");
        nameField.setMaxWidth(300);
        nameField.setFont(Font.font("Arial", 14));

        // Membership card input
        Label cardLabel = new Label("Membership Card (optional):");
        cardLabel.setFont(Font.font("Arial", 16));
        cardLabel.setStyle("-fx-text-fill: white;");

        TextField cardField = new TextField();
        cardField.setPromptText("Card Number");
        cardField.setMaxWidth(300);
        cardField.setFont(Font.font("Arial", 14));

        // Start shopping button
        Button startButton = new Button("Start Shopping");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);

        startButton.setOnAction(e -> {
            String customerName = nameField.getText().trim();
            if (customerName.isEmpty()) {
                customerName = "Guest";
            }

            // Create customer
            currentCustomer = new Customer(customerName);

            // Apply membership card if provided
            String cardNumber = cardField.getText().trim();
            if (!cardNumber.isEmpty()) {
                MembershipCard card = new MembershipCard(cardNumber);
                card.setPoints(100); // Give some initial points for demo
                currentCustomer.setMembershipCard(card);
            }

            // Show shopping view
            showShoppingView();
        });

        welcomeLayout.getChildren().addAll(
                titleLabel, subtitleLabel,
                new Label(), // Spacer
                nameLabel, nameField,
                cardLabel, cardField,
                new Label(), // Spacer
                startButton
        );

        mainScene = new Scene(welcomeLayout, 800, 600);
        primaryStage.setScene(mainScene);
    }

    /**
     * Shows the main shopping view with product selection and cart.
     */
    private void showShoppingView() {
        BorderPane mainLayout = new BorderPane();

        // Top: Store info and customer info
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);

        // Center: Product selection (simplified for now)
        VBox centerPanel = createProductSelectionPanel();

        // Right: Cart view
        CartView cartView = new CartView(currentCustomer.getCart());

        // Override checkout button action
        cartView.getCheckoutButton().setOnAction(e -> {
            if (currentCustomer.getCart().isEmpty()) {
                showAlert("Empty Cart", "Please add items to cart first.", Alert.AlertType.WARNING);
                return;
            }
            showCheckoutView(cartView);
        });

        // Split pane
        SplitPane splitPane = new SplitPane(centerPanel, cartView);
        splitPane.setDividerPositions(0.6);
        mainLayout.setCenter(splitPane);

        Scene shoppingScene = new Scene(mainLayout, 1200, 700);
        primaryStage.setScene(shoppingScene);
    }

    /**
     * Creates the top bar with store and customer info.
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

        Label customerLabel = new Label("👤 " + currentCustomer.getName());
        customerLabel.setFont(Font.font("Arial", 16));
        customerLabel.setStyle("-fx-text-fill: white;");

        if (currentCustomer.hasMembershipCard()) {
            Label memberLabel = new Label("⭐ Member: " +
                    currentCustomer.getMembershipCard().getPoints() + " pts");
            memberLabel.setFont(Font.font("Arial", 14));
            memberLabel.setStyle("-fx-text-fill: #FFD700;");
            topBar.getChildren().addAll(storeLabel, spacer, customerLabel, memberLabel);
        } else {
            topBar.getChildren().addAll(storeLabel, spacer, customerLabel);
        }

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

        // Create product buttons
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
     * Creates a product box with add to cart button.
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
            currentCustomer.addToCart(product, qty);
            showAlert("Added to Cart",
                    String.format("Added %dx %s to cart", qty, product.getName()),
                    Alert.AlertType.INFORMATION);
            // Refresh the shopping view to update cart
            showShoppingView();
        });

        box.getChildren().addAll(nameLabel, priceLabel, stockLabel, qtySpinner, addButton);

        return box;
    }

    /**
     * Shows the checkout view.
     */
    private void showCheckoutView(CartView previousCartView) {
        CheckoutView checkoutView = new CheckoutView(currentCustomer, currentCustomer.getCart());

        // Override back button
        checkoutView.getBackButton().setOnAction(e -> showShoppingView());

        // Override process payment button
        checkoutView.getProcessPaymentButton().setOnAction(e -> {
            processCheckout(checkoutView);
        });

        Scene checkoutScene = new Scene(checkoutView, 1000, 700);
        primaryStage.setScene(checkoutScene);
    }

    /**
     * Processes the checkout and shows receipt.
     */
    private void processCheckout(CheckoutView checkoutView) {
        try {
            String amountText = checkoutView.getAmountReceived();
            if (amountText == null || amountText.trim().isEmpty()) {
                showAlert("Invalid Payment", "Please enter payment amount.", Alert.AlertType.WARNING);
                return;
            }

            double amountReceived = Double.parseDouble(amountText);

            // Calculate total (this logic should match CheckoutController)
            double subtotal = currentCustomer.getCart().computeSubtotal();
            double afterDiscount = subtotal;

            if (checkoutView.isSeniorDiscount()) {
                afterDiscount = DiscountPolicy.applySeniorDiscount(afterDiscount);
            }

            if (checkoutView.isUseMembershipPoints() && currentCustomer.hasMembershipCard()) {
                afterDiscount = DiscountPolicy.applyMembershipDiscount(currentCustomer, afterDiscount);
            }

            double vat = DiscountPolicy.calculateVAT(afterDiscount);
            double total = afterDiscount + vat;

            if (amountReceived < total) {
                showAlert("Insufficient Payment",
                        String.format("Need ₱%.2f more", total - amountReceived),
                        Alert.AlertType.WARNING);
                return;
            }

            // Create payment and transaction
            Payment payment = new Payment(amountReceived, total);
            Transaction transaction = currentCustomer.checkOut(store);
            transaction.setPayment(payment);

            // Update membership points
            if (currentCustomer.hasMembershipCard()) {
                MembershipCard card = currentCustomer.getMembershipCard();
                if (checkoutView.isUseMembershipPoints()) {
                    int pointsToRedeem = Math.min(card.getPoints(), (int)subtotal);
                    card.redeemPoints(pointsToRedeem);
                }
                card.addPoints(total);
            }

            // Generate and show receipt
            Receipt receipt = transaction.generateReceipt();
            ReceiptView receiptView = new ReceiptView(receipt);
            receiptView.show();

            // Reset and go back to welcome
            showAlert("Transaction Complete",
                    String.format("Change: ₱%.2f\nThank you for shopping!", payment.computeChange()),
                    Alert.AlertType.INFORMATION);

            // Go back to welcome screen
            currentCustomer = null;
            showWelcomeScreen();

        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Please enter a valid amount.", Alert.AlertType.ERROR);
        }
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