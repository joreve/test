import java.time.LocalDate;

/**
 * Represents a product available in the convenience store inventory.
 * Stores information like ID, name, price, stock, category, brand, variant,
 * and expiration date.
 * 
 * @author Joreve P. De Jesus
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
    
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
        this.isPerishable = (expirationDate != null);
    }

    public int getProductID() { return productID; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public Category getCategory() { return category; }
    public String getBrand() { return brand; }
    public String getVariant() { return variant; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public boolean isPerishable() { return isPerishable; }
}
