/**
 * CartItem class represents a single line item in a shopping cart.
 * Contains a product and the quantity of that product.
 */
public class CartItem {
    private Product product;
    private int quantity;

    /**
     * Constructs a CartItem with the specified product and quantity.
     * @param product the product in this cart item
     * @param quantity the quantity of the product
     */
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Computes the line total (price × quantity) for this cart item.
     *
     * @return the total price for this line item
     */
    public double computeLineTotal() {
        return product.getPrice() * quantity;
    }

    /**
     * Gets the product in this cart item.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Gets the quantity of the product.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        }
    }
}