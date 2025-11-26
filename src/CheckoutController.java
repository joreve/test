import javafx.scene.control.Alert;

/**
 * CheckoutController handles the payment process.
 * Manages pricing calculations, discounts, membership cards, and transaction completion.
 * ALL business logic moved here from CheckoutView.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class CheckoutController {
    private Customer customer;
    private Cart cart;
    private ConvenienceStore store;
    private DataManager dataManager;
    private MainApplication mainApp;
    private CheckoutView view;

    // Current pricing state
    private double currentSubtotal;
    private double currentDiscount;
    private double currentVAT;
    private double currentTotal;

    public CheckoutController(Customer customer, Cart cart, ConvenienceStore store,
                             DataManager dataManager, MainApplication mainApp) {
        this.customer = customer;
        this.cart = cart;
        this.store = store;
        this.dataManager = dataManager;
        this.mainApp = mainApp;
    }
    
    public void setView(CheckoutView view) {
        this.view = view;
        // Initial calculation when view is set
        recalculatePricing();
    }

    /**
     * Applies a membership card to the customer.
     */
    public void handleApplyMembershipCard(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            showAlert("Invalid Card", "Please enter a card number.", Alert.AlertType.WARNING);
            return;
        }

        // Create and apply membership card
        MembershipCard card = new MembershipCard(cardNumber.trim());
        customer.setMembershipCard(card);

        showAlert("Card Applied", "Membership card successfully applied!", Alert.AlertType.INFORMATION);
        view.updateMembershipDisplay();
        recalculatePricing();
    }

    /**
     * Recalculates pricing with current discounts and settings.
     * This is where ALL pricing logic lives.
     */
    public void recalculatePricing() {
        currentSubtotal = cart.computeSubtotal();
        currentDiscount = 0.0;

        double afterDiscount = currentSubtotal;

        // Apply senior discount first (if selected)
        if (view.isSeniorDiscountSelected()) {
            double seniorDiscounted = DiscountPolicy.applySeniorDiscount(afterDiscount);
            currentDiscount += (afterDiscount - seniorDiscounted);
            afterDiscount = seniorDiscounted;
        }

        // Apply membership points discount (if selected and available)
        if (view.isUseMembershipPointsSelected() && customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            double pointsDiscount = Math.min(card.getDiscount(), afterDiscount);
            currentDiscount += pointsDiscount;
            afterDiscount -= pointsDiscount;
        }

        // Calculate VAT
        currentVAT = DiscountPolicy.calculateVAT(afterDiscount);

        // Calculate final total
        currentTotal = afterDiscount + currentVAT;

        // Update view display
        view.displayPricing(currentSubtotal, currentDiscount, currentVAT, currentTotal);
    }

    /**
     * Handles amount received field changes to calculate change.
     */
    public void handleAmountChanged(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            double change = amount - currentTotal;
            view.displayChange(change);
        } catch (NumberFormatException e) {
            view.displayChange(-1); // Indicates insufficient/invalid
        }
    }

    /**
     * Processes the payment and completes the transaction.
     */
    public void handleProcessPayment() {
        // Validate amount received
        String amountText = view.getAmountReceived();
        if (amountText == null || amountText.trim().isEmpty()) {
            showAlert("Invalid Payment", "Please enter payment amount.", Alert.AlertType.WARNING);
            return;
        }

        double amountReceived;
        try {
            amountReceived = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            showAlert("Invalid Payment", "Please enter a valid number.", Alert.AlertType.ERROR);
            return;
        }

        if (amountReceived < currentTotal) {
            showAlert("Insufficient Payment",
                    String.format("Payment is insufficient. Need ₱%.2f more.",
                            currentTotal - amountReceived),
                    Alert.AlertType.WARNING);
            return;
        }

        // Create payment object
        Payment payment = new Payment(amountReceived, currentTotal);

        // Update membership card if used
        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();

            // Redeem points if used
            if (view.isUseMembershipPointsSelected()) {
                int pointsToRedeem = Math.min(card.getPoints(), (int)currentDiscount);
                card.redeemPoints(pointsToRedeem);
            }

            // Award new points based on total spent
            card.addPoints(currentTotal);
        }

        // Create transaction
        Transaction transaction = customer.checkOut(store);
        transaction.setPayment(payment);

        // Save everything
        if (customer.hasMembershipCard()) {
            dataManager.updateCustomer(customer);
        }
        dataManager.saveProducts(store.getInventory().getProducts());
        dataManager.saveTransaction(transaction);

        // Generate and save receipt
        Receipt receipt = transaction.generateReceipt();
        receipt.setDataManager(dataManager);
        receipt.saveToFile();

        // Show receipt window
        ReceiptView receiptView = new ReceiptView(receipt);
        receiptView.show();

        // Success message
        showAlert("Payment Successful",
                String.format("Change: ₱%.2f\nReceipt saved automatically.\nThank you for shopping!",
                        payment.computeChange()),
                Alert.AlertType.INFORMATION);

        // Return to shopping view
        mainApp.showCustomerView();
    }

    /**
     * Handles going back to cart.
     */
    public void handleBack() {
        mainApp.showCartView();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}