import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * CheckoutView handles the payment interface.
 * Displays order summary, accepts payment, shows pricing.
 * NO business logic - all calculations done in CheckoutController.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class CheckoutView extends BorderPane {
    private Customer customer;
    private Cart cart;
    private CheckoutController controller;

    // UI Components
    private ListView<HBox> orderSummaryList;
    private Label subtotalLabel;
    private Label discountLabel;
    private Label vatLabel;
    private Label totalLabel;

    private TextField membershipCardField;
    private Button applyCardButton;
    private CheckBox useMembershipCheckBox;
    private CheckBox isSeniorCheckBox;
    private Label availablePointsLabel;

    private TextField amountReceivedField;
    private Label changeLabel;
    private Button processPaymentButton;
    private Button backButton;

    public CheckoutView(Customer customer, Cart cart) {
        this.customer = customer;
        this.cart = cart;
        initializeUI();
    }
    
    /**
     * Injects the controller after view creation.
     */
    public void setController(CheckoutController controller) {
        this.controller = controller;
    }

    private void initializeUI() {
        // Top: Title
        Label titleLabel = new Label("Checkout");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setPadding(new Insets(15));
        titleLabel.setStyle("-fx-text-fill: white;");

        HBox topBox = new HBox(titleLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: #4CAF50;");
        setTop(topBox);

        // Left: Order Summary
        VBox leftPanel = createOrderSummaryPanel();

        // Right: Payment Panel
        VBox rightPanel = createPaymentPanel();

        // Center: Split Left and Right
        HBox centerBox = new HBox(20, leftPanel, rightPanel);
        centerBox.setPadding(new Insets(20));
        setCenter(centerBox);

        // Bottom: Back Button
        backButton = new Button("← Back to Cart");
        backButton.setStyle("-fx-font-size: 14px;");
        backButton.setOnAction(e -> controller.handleBack());

        HBox bottomBox = new HBox(backButton);
        bottomBox.setPadding(new Insets(15));
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        setBottom(bottomBox);

        updateMembershipDisplay();
        refreshOrderSummary();
    }

    private VBox createOrderSummaryPanel() {
        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        orderSummaryList = new ListView<>();
        orderSummaryList.setPrefWidth(400);
        orderSummaryList.setPrefHeight(300);

        VBox summaryBox = new VBox(10, summaryTitle, orderSummaryList);
        summaryBox.setPadding(new Insets(15));
        summaryBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");

        return summaryBox;
    }

    private VBox createPaymentPanel() {
        VBox paymentBox = new VBox(15);
        paymentBox.setPadding(new Insets(15));
        paymentBox.setPrefWidth(350);
        paymentBox.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-background-color: white;");

        // Membership Section
        Label membershipTitle = new Label("Membership Card");
        membershipTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        membershipCardField = new TextField();
        membershipCardField.setPromptText("Enter card number");
        membershipCardField.setPrefWidth(250);

        applyCardButton = new Button("Apply Card");
        applyCardButton.setOnAction(e -> controller.handleApplyMembershipCard(membershipCardField.getText()));

        availablePointsLabel = new Label("Available Points: 0");
        availablePointsLabel.setFont(Font.font("Arial", 12));

        useMembershipCheckBox = new CheckBox("Use membership points");
        useMembershipCheckBox.setOnAction(e -> controller.recalculatePricing());

        VBox membershipBox = new VBox(8, membershipTitle, membershipCardField,
                applyCardButton, availablePointsLabel, useMembershipCheckBox);

        // Senior Discount
        isSeniorCheckBox = new CheckBox("Apply Senior Citizen Discount (20%)");
        isSeniorCheckBox.setOnAction(e -> controller.recalculatePricing());

        Separator sep1 = new Separator();

        // Price Summary
        subtotalLabel = new Label("Subtotal: ₱0.00");
        discountLabel = new Label("Discount: -₱0.00");
        vatLabel = new Label("VAT (12%): ₱0.00");
        totalLabel = new Label("TOTAL: ₱0.00");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox priceBox = new VBox(5, subtotalLabel, discountLabel, vatLabel,
                new Separator(), totalLabel);

        Separator sep2 = new Separator();

        // Payment Section
        Label paymentTitle = new Label("Payment");
        paymentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label amountLabel = new Label("Amount Received:");
        amountReceivedField = new TextField();
        amountReceivedField.setPromptText("Enter amount");
        amountReceivedField.textProperty().addListener((obs, old, newVal) -> {
            controller.handleAmountChanged(newVal);
        });

        changeLabel = new Label("Change: ₱0.00");
        changeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        changeLabel.setStyle("-fx-text-fill: green;");

        processPaymentButton = new Button("Process Payment");
        processPaymentButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        processPaymentButton.setPrefWidth(250);
        processPaymentButton.setPrefHeight(40);
        processPaymentButton.setOnAction(e -> controller.handleProcessPayment());

        VBox paymentInputBox = new VBox(8, paymentTitle, amountLabel,
                amountReceivedField, changeLabel, processPaymentButton);

        paymentBox.getChildren().addAll(membershipBox, isSeniorCheckBox, sep1,
                priceBox, sep2, paymentInputBox);

        return paymentBox;
    }

    /**
     * Refreshes the order summary list.
     */
    private void refreshOrderSummary() {
        orderSummaryList.getItems().clear();
        for (CartItem item : cart.getItems()) {
            HBox itemBox = createOrderItemBox(item);
            orderSummaryList.getItems().add(itemBox);
        }
    }

    private HBox createOrderItemBox(CartItem item) {
        Product product = item.getProduct();

        Label nameLabel = new Label(product.getName());
        nameLabel.setPrefWidth(200);

        Label qtyLabel = new Label("x" + item.getQuantity());
        qtyLabel.setPrefWidth(50);

        Label priceLabel = new Label(String.format("₱%.2f", item.computeLineTotal()));
        priceLabel.setAlignment(Pos.CENTER_RIGHT);
        priceLabel.setPrefWidth(100);

        HBox itemBox = new HBox(10, nameLabel, qtyLabel, priceLabel);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setPadding(new Insets(5));

        return itemBox;
    }

    /**
     * Updates membership card display.
     * Called by controller after card is applied.
     */
    public void updateMembershipDisplay() {
        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            membershipCardField.setText(card.getCardNumber());
            membershipCardField.setDisable(true);
            applyCardButton.setDisable(true);
            applyCardButton.setText("Card Applied");
            applyCardButton.setStyle("-fx-background-color: #ccc; -fx-text-fill: #666;");
            availablePointsLabel.setText("Available Points: " + card.getPoints());
            useMembershipCheckBox.setDisable(false);
        } else {
            membershipCardField.setDisable(false);
            applyCardButton.setDisable(false);
            applyCardButton.setText("Apply Card");
            applyCardButton.setStyle("");
            availablePointsLabel.setText("No membership card");
            useMembershipCheckBox.setDisable(true);
        }
    }

    /**
     * Displays the pricing information.
     * Called by controller after calculations.
     */
    public void displayPricing(double subtotal, double discount, double vat, double total) {
        subtotalLabel.setText(String.format("Subtotal: ₱%.2f", subtotal));
        discountLabel.setText(String.format("Discount: -₱%.2f", discount));
        vatLabel.setText(String.format("VAT (12%%): ₱%.2f", vat));
        totalLabel.setText(String.format("TOTAL: ₱%.2f", total));
    }

    /**
     * Displays the change amount.
     * Called by controller when amount changes.
     */
    public void displayChange(double change) {
        if (change < 0) {
            changeLabel.setText("Change: Insufficient Payment");
            changeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            changeLabel.setText(String.format("Change: ₱%.2f", change));
            changeLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    // Getters for controller to check checkbox states
    public boolean isUseMembershipPointsSelected() {
        return useMembershipCheckBox.isSelected();
    }

    public boolean isSeniorDiscountSelected() {
        return isSeniorCheckBox.isSelected();
    }

    public String getAmountReceived() {
        return amountReceivedField.getText();
    }
}