/**
 * Customer class represents a customer in the convenience store.
 * Extends the User class and includes shopping cart functionality
 * and optional membership card support.
 * 
 * @author Dana Ysabelle A. Pelagio
 */
public class Customer extends User {
    private MembershipCard membershipCard;
    private Cart cart;

    /**
     * Constructs a Customer with the specified name.
     * Initializes a new empty cart for the customer.
     *
     * @param name the name of the customer
     */
    public Customer(String name) {
        super(name);
        this.cart = new Cart();
        this.membershipCard = null;
    }

    /**
     * Constructs a Customer with the specified name and membership card.
     * Initializes a new empty cart for the customer.
     *
     * @param name the name of the customer
     * @param membershipCard the membership card of the customer
     */
    public Customer(String name, MembershipCard membershipCard) {
        super(name);
        this.cart = new Cart();
        this.membershipCard = membershipCard;
    }

    /**
     * Adds a product to the customer's cart with the specified quantity.
     * @param product the product to add to the cart
     * @param quantity the quantity of the product to add
     */
    public void addToCart(Product product, int quantity) {
        if (product != null && quantity > 0) {
            cart.addItem(product, quantity);
        }
    }

    /**
     * Views the running total of all items in the cart.
     * @return the subtotal of all items in the cart
     */
    public double viewRunningTotal() {
        return cart.computeSubtotal();
    }

    /**
     * Processes the checkout for this customer at the specified store.
     * Creates and returns a transaction for the purchase.
     *
     * @param store the convenience store where checkout is being processed
     * @return the transaction created from this checkout
     */
    public Transaction checkOut(ConvenienceStore store) {
        double subtotal = this.cart.computeSubtotal();

        Transaction transaction = new Transaction(
                "TXN-" + System.currentTimeMillis(), // Generate unique transaction ID
                this,                                 // The customer (this)
                this.cart.getItems(),                // Copy of cart items
                subtotal                              // The subtotal (uses new constructor)
        );

        store.getInventory().autoReduceStock(this.cart);
        store.saveToSalesHistory(transaction);
        this.cart = new Cart();

        return transaction;
    }

    /**
     * Checks if the customer has a membership card.
     * @return true if customer has a membership card, false otherwise
     */
    public boolean hasMembershipCard() {
        return membershipCard != null;
    }

    public MembershipCard getMembershipCard() {
        return membershipCard;
    }

    public Cart getCart() {
        return cart;
    }

    public void setMembershipCard(MembershipCard membershipCard) {
        this.membershipCard = membershipCard;
    }
}