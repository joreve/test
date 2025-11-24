import java.time.LocalDate;

/**
 * Represents a product available in the convenience store inventory.
 * Stores information like ID, name, price, stock, category, brand, variant,
 * and expiration date.
 */
class Product {
    private int productID;
    private String name;
    private double price;
    private int stock;
    private Category category;
    private String brand;
    private String variant;
    private LocalDate expirationDate;
    private boolean isPerishable;

    /**
     * Constructs a Product with essential general attributes.
     * Brand, variant, and expiration date are set to null.
     *
     * @param productID The unique ID of the product.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param stock The initial stock quantity.
     * @param category The product's category.
     */
    public Product(int productID, String name, double price, int stock, Category category) {
        this(productID, name, price, stock, category, null, null, null);
    }

    /**
     * Constructs a Product with all possible attributes, including brand, variant, and expiration date.
     * The `isPerishable` flag is set based on the presence of an `expirationDate`.
     *
     * @param productID The unique ID of the product.
     * @param name The name of the product.
     * @param price The price of the product.
     * @param stock The initial stock quantity.
     * @param category The product's category.
     * @param brand The brand of the product (can be null).
     * @param variant The variant or size of the product (can be null).
     * @param expirationDate The expiration date (can be null).
     */
    public Product(int productID, String name, double price, int stock,
                   Category category, String brand, String variant, LocalDate expirationDate) {
        this.productID = productID;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.variant = variant;
        this.expirationDate = expirationDate;
        this.isPerishable = (expirationDate != null);
    }

    /**
     * Decrements the stock of the product by the specified quantity.
     *
     * @param quantity The number of units to remove from stock.
     * @return true if stock was successfully reduced, false if there was insufficient stock.
     */
    public boolean reduceStock(int quantity) {
        if (quantity > 0 && quantity <= stock) {
            stock -= quantity;
            return true;
        }
        else {
            System.out.println("Insufficient stock for " + name + ".\n");
            return false;
        }
    }

    /**
     * Increases the stock of the product by the specified quantity.
     *
     * @param quantity The number of units to add to stock. Must be positive.
     */
    public void restock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        }
        else {
            System.out.println("Invalid Quantity.\n");
        }
    }
    
    /**
     * Gets the product's unique ID.
     * @return the product ID.
     */
    public int getProductID() { return productID; }
    /**
     * Gets the name of the product.
     * @return the product name.
     */
    public String getName() { return name; }
    /**
     * Gets the price of the product.
     * @return the product price.
     */
    public double getPrice() { return price; }
    /**
     * Gets the current stock quantity.
     * @return the stock quantity.
     */
    public int getStock() { return stock; }
    /**
     * Gets the category of the product.
     * @return the product category.
     */
    public Category getCategory() { return category; }
    /**
     * Gets the brand of the product.
     * @return the product brand, or null if not applicable.
     */
    public String getBrand() { return brand; }
    /**
     * Gets the variant or size of the product.
     * @return the product variant, or null if not applicable.
     */
    public String getVariant() { return variant; }
    /**
     * Gets the expiration date of the product.
     * @return the expiration date, or null if non-perishable.
     */
    public LocalDate getExpirationDate() { return expirationDate; }
    /**
     * Checks if the product is perishable (has an expiration date).
     * @return true if perishable, false otherwise.
     */
    public boolean isPerishable() { return isPerishable; }
}
