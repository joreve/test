import java.util.ArrayList;

/**
 * Cart class represents a shopping cart that holds items
 * selected by a customer for purchase.
 * 
 * @author Dana Ysabelle A. Pelagio
 */
public class Cart {
    private ArrayList<CartItem> items;

    /**
     * Constructs an empty Cart.
     */
    public Cart() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds a product to the cart with the specified quantity.
     * If the product already exists in the cart, updates the quantity.
     *
     * @param product the product to add
     * @param quantity the quantity to add
     */
    public void addItem(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return;
        }

        for (CartItem item : items) {
            if (item.getProduct().getProductID() == product.getProductID()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        items.add(new CartItem(product, quantity));
    }

    /**
     * Removes a product from the cart.
     *
     * @param product the product to remove
     */
    public void removeItem(Product product) {
        if (product == null) {
            return;
        }

        items.removeIf(item -> item.getProduct().getProductID() == product.getProductID());
    }

    /**
     * Computes the subtotal of all items in the cart.
     *
     * @return the subtotal amount (without tax or discounts)
     */
    public double computeSubtotal() {
        double subtotal = 0.0;
        for (CartItem item : items) {
            subtotal += item.computeLineTotal();
        }
        return subtotal;
    }

    /**
     * Lists all items in the cart to the console.
     */
    public void listItems() {
        if (items.isEmpty()) {
            System.out.println("Cart is empty.");
            return;
        }

        System.out.println("\n===== CART ITEMS =====");
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            Product product = item.getProduct();
            System.out.printf("%d. %s x%d - P%.2f\n",
                    i + 1,
                    product.getName(),
                    item.getQuantity(),
                    item.computeLineTotal());
        }
        System.out.printf("\nSubtotal: P%.2f\n", computeSubtotal());
        System.out.println("======================\n");
    }

    /**
     * Gets all items in the cart.
     * @return ArrayList of CartItems
     */
    public ArrayList<CartItem> getItems() {
        return items;
    }

    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Checks if the cart is empty.
     *
     * @return true if cart has no items, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}