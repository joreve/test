import javafx.scene.control.Alert;

/**
 * CheckoutController handles the checkout process including
 * payment validation, discount application, and transaction creation.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class CheckoutController {
    private Customer customer;
    private Cart cart;
    private CheckoutView checkoutView;

    private double currentTotal;
    private double currentSubtotal;
    private double currentDiscount;
    private double currentVAT;

    /**
     * Constructs a CheckoutController.
     *
     * @param customer the customer checking out
     * @param cart the shopping cart
     * @param checkoutView the checkout view
     */
    public CheckoutController(Customer customer, Cart cart, CheckoutView checkoutView) {
        this.customer = customer;
        this.cart = cart;
        this.checkoutView = checkoutView;
    }

    /**
     * Applies a membership card to the customer.
     *
     * @param cardNumber the card number to apply
     */
    public void handleApplyMembershipCard(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            showAlert("Invalid Card", "Please enter a card number.", Alert.AlertType.WARNING);
            return;
        }

        // In a real system, you'd validate this against a database
        // For now, we'll just create a new card
        MembershipCard card = new MembershipCard(cardNumber.trim());
        customer.setMembershipCard(card);

        showAlert("Card Applied", "Membership card successfully applied!", Alert.AlertType.INFORMATION);
        checkoutView.refreshDisplay();
    }

    /**
     * Recalculates the total with current discounts and settings.
     */
    public void handleRecalculate() {
        currentSubtotal = cart.computeSubtotal();
        currentDiscount = 0.0;

        // Apply senior discount first (if applicable)
        double afterSeniorDiscount = currentSubtotal;
        if (checkoutView.isSeniorDiscount()) {
            afterSeniorDiscount = DiscountPolicy.applySeniorDiscount(currentSubtotal);
            currentDiscount += (currentSubtotal - afterSeniorDiscount);
        }

        // Apply membership points discount
        double afterMembershipDiscount = afterSeniorDiscount;
        if (checkoutView.isUseMembershipPoints() && customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            double pointsDiscount = Math.min(card.getDiscount(), afterSeniorDiscount);
            afterMembershipDiscount = afterSeniorDiscount - pointsDiscount;
            currentDiscount += pointsDiscount;
        }

        // Calculate VAT
        currentVAT = DiscountPolicy.calculateVAT(afterMembershipDiscount);

        // Calculate final total
        currentTotal = afterMembershipDiscount + currentVAT;

        // Update display
        checkoutView.updatePriceLabels(currentSubtotal, currentDiscount, currentVAT, currentTotal);
    }

    /**
     * Handles amount received field changes to show change.
     *
     * @param amountText the amount text entered
     */
    public void handleAmountChanged(String amountText) {
        try {
            double amount = Double.parseDouble(amountText);
            double change = amount - currentTotal;
            checkoutView.updateChangeLabel(change);
        } catch (NumberFormatException e) {
            checkoutView.updateChangeLabel(-1); // Shows insufficient payment
        }
    }

    /**
     * Processes the payment and creates a transaction.
     */
    public void handleProcessPayment() {
        // Validate amount received
        String amountText = checkoutView.getAmountReceived();
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

        // Create transaction
        Transaction transaction = customer.checkOut(null); // Store will be passed by main controller
        transaction.setPayment(payment);

        // Update membership points if card is used
        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();

            // Redeem points if used
            if (checkoutView.isUseMembershipPoints()) {
                int pointsToRedeem = Math.min(card.getPoints(), (int)currentDiscount);
                card.redeemPoints(pointsToRedeem);
            }

            // Award new points based on total spent
            card.addPoints(currentTotal);
        }

        // Generate receipt
        Receipt receipt = transaction.generateReceipt();

        // Show receipt in new window
        showReceiptWindow(receipt);

        // Success message
        showAlert("Payment Successful",
                String.format("Change: ₱%.2f\nReceipt saved to file.", payment.computeChange()),
                Alert.AlertType.INFORMATION);
    }

    /**
     * Shows the receipt in a new window.
     *
     * @param receipt the receipt to display
     */
    private void showReceiptWindow(Receipt receipt) {
        // This will be implemented by creating ReceiptView, temporary muna
        receipt.saveToFile();
        receipt.display();
    }

    /**
     * Handles going back to the cart view.
     */
    public void handleBack() {
        System.out.println("Going back to cart...");
    }

    /**
     * Shows an alert dialog.
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