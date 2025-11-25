import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * CartView displays the shopping cart contents with options to
 * modify quantities, remove items, and proceed to checkout.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class CartView extends BorderPane {
    private Cart cart;
    private ListView<HBox> cartListView;
    private Label subtotalLabel;
    private Label itemCountLabel;
    private Button checkoutButton;
    private Button clearCartButton;

    private CartController controller;

    /**
     * Constructs a CartView for the specified cart.
     *
     * @param cart the shopping cart to display
     */
    public CartView(Cart cart) {
        this.cart = cart;
        this.controller = new CartController(cart, this);
        initializeUI();
    }

    /**
     * Initializes the user interface components.
     */
    private void initializeUI() {
        // Top: Title
        Label titleLabel = new Label("Shopping Cart");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setPadding(new Insets(15));

        itemCountLabel = new Label("Items: 0");
        itemCountLabel.setFont(Font.font("Arial", 14));

        VBox topBox = new VBox(5, titleLabel, itemCountLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        setTop(topBox);

        // Center: Cart Items List
        cartListView = new ListView<>();
        cartListView.setStyle("-fx-font-size: 14px;");
        cartListView.setPrefHeight(400);
        setCenter(cartListView);

        // Bottom: Subtotal and Buttons
        subtotalLabel = new Label("Subtotal: ₱0.00");
        subtotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        checkoutButton = new Button("Proceed to Checkout");
        checkoutButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        checkoutButton.setPrefWidth(200);
        checkoutButton.setOnAction(e -> controller.handleCheckout());

        clearCartButton = new Button("Clear Cart");
        clearCartButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        clearCartButton.setPrefWidth(150);
        clearCartButton.setOnAction(e -> controller.handleClearCart());

        HBox buttonBox = new HBox(15, clearCartButton, checkoutButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox bottomBox = new VBox(15, subtotalLabel, buttonBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setStyle("-fx-background-color: #f5f5f5;");
        setBottom(bottomBox);

        refreshCartDisplay();
    }

    /**
     * Refreshes the cart display with current items.
     */
    public void refreshCartDisplay() {
        cartListView.getItems().clear();

        if (cart.isEmpty()) {
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setFont(Font.font("Arial", 14));
            HBox emptyBox = new HBox(emptyLabel);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(20));
            cartListView.getItems().add(emptyBox);

            itemCountLabel.setText("Items: 0");
            subtotalLabel.setText("Subtotal: ₱0.00");
            checkoutButton.setDisable(true);
            clearCartButton.setDisable(true);
            return;
        }

        checkoutButton.setDisable(false);
        clearCartButton.setDisable(false);

        for (CartItem item : cart.getItems()) {
            HBox itemBox = createCartItemBox(item);
            cartListView.getItems().add(itemBox);
        }

        itemCountLabel.setText("Items: " + cart.getItems().size());
        subtotalLabel.setText(String.format("Subtotal: ₱%.2f", cart.computeSubtotal()));
    }

    /**
     * Creates a visual box for a single cart item.
     *
     * @param item the cart item to display
     * @return HBox containing item details and controls
     */
    private HBox createCartItemBox(CartItem item) {
        Product product = item.getProduct();

        // Product info
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label priceLabel = new Label(String.format("₱%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", 12));

        VBox infoBox = new VBox(5, nameLabel, priceLabel);
        infoBox.setPrefWidth(200);

        // Quantity controls
        Label qtyLabel = new Label("Qty:");
        Spinner<Integer> qtySpinner = new Spinner<>(1, product.getStock(), item.getQuantity());
        qtySpinner.setPrefWidth(80);
        qtySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            controller.handleQuantityChange(item, newVal);
        });

        HBox qtyBox = new HBox(5, qtyLabel, qtySpinner);
        qtyBox.setAlignment(Pos.CENTER_LEFT);

        // Line total
        Label lineTotalLabel = new Label(String.format("₱%.2f", item.computeLineTotal()));
        lineTotalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lineTotalLabel.setPrefWidth(100);
        lineTotalLabel.setAlignment(Pos.CENTER_RIGHT);

        // Remove button
        Button removeBtn = new Button("Remove");
        removeBtn.setStyle("-fx-background-color: #ff5722; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> controller.handleRemoveItem(item));

        // Combine everything
        HBox itemBox = new HBox(15, infoBox, qtyBox, lineTotalLabel, removeBtn);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(10));
        itemBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0;");

        return itemBox;
    }

    public Button getCheckoutButton() {
        return checkoutButton;
    }

    public Cart getCart() {
        return cart;
    }
}