/**
 * TransactionController manages the overall transaction flow
 * from cart to checkout to receipt generation.
 * Coordinates between different views and controllers.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class TransactionController {
    private Customer customer;
    private ConvenienceStore store;

    private CartView cartView;
    private CheckoutView checkoutView;

    private Transaction currentTransaction;

    /**
     * Constructs a TransactionController.
     *
     * @param customer the customer making the transaction
     * @param store the convenience store
     */
    public TransactionController(Customer customer, ConvenienceStore store) {
        this.customer = customer;
        this.store = store;
    }

    /**
     * Initializes the cart view.
     *
     * @return the cart view
     */
    public CartView initializeCartView() {
        cartView = new CartView(customer.getCart());
        return cartView;
    }

    /**
     * Transitions from cart to checkout.
     *
     * @return the checkout view
     */
    public CheckoutView proceedToCheckout() {
        if (customer.getCart().isEmpty()) {
            return null;
        }

        checkoutView = new CheckoutView(customer, customer.getCart());
        return checkoutView;
    }

    /**
     * Processes the complete transaction and generates receipt.
     *
     * @param payment the payment information
     * @return the generated receipt
     */
    public Receipt processTransaction(Payment payment) {
        // Create transaction
        currentTransaction = customer.checkOut(store);
        currentTransaction.setPayment(payment);

        // Update membership points if applicable
        if (customer.hasMembershipCard()) {
            MembershipCard card = customer.getMembershipCard();
            card.addPoints(currentTransaction.getTotalCost());
        }

        // Generate and return receipt
        Receipt receipt = currentTransaction.generateReceipt();
        receipt.saveToFile();

        return receipt;
    }

    /**
     * Shows the receipt in a new window.
     *
     * @param receipt the receipt to display
     */
    public void showReceipt(Receipt receipt) {
        ReceiptView receiptView = new ReceiptView(receipt);
        receiptView.show();
    }

    /**
     * Resets the transaction for a new purchase.
     */
    public void resetTransaction() {
        currentTransaction = null;
        customer.getCart().clear();
        if (cartView != null) {
            cartView.refreshCartDisplay();
        }
    }

    /**
     * Gets the current transaction.
     *
     * @return the current transaction, or null if none exists
     */
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets the store.
     *
     * @return the convenience store
     */
    public ConvenienceStore getStore() {
        return store;
    }
}