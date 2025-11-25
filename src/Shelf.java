import java.util.ArrayList;

/**
 * Represents a shelf or display area in the store, dedicated to a specific category.
 * It holds a collection of Product objects that match its category.
 * 
 * @author Joreve P. De Jesus
 */
public class Shelf {
    private Category category;
    private ArrayList<Product> products;

    /**
     * Constructs a new Shelf for a specific product category.
     *
     * @param category The category (main and sub) of products this shelf will hold.
     */
    public Shelf(Category category) {
        this.category = category;
        this.products = new ArrayList<>();
    }

    /**
     * Adds a product to the shelf, but only if the product's category matches the shelf's category.
     *
     * @param product The product to add.
     */
    public void addProduct(Product product) {
        if (product.getCategory().getName().equalsIgnoreCase(category.getName())) {
            products.add(product);
        } 
        else {
            System.out.println("Product category does not match shelf category.\n");
        }
    }
    
    /**
     * Prints the details of all products currently on the shelf to the console,
     * including category header and specific product details like brand, variant, and expiration.
     */
    public void displayShelf() {
        System.out.println("=== " + category.getName().toUpperCase() + " - " + category.getType().toUpperCase() + " ===");

        for (Product p : products) {
            System.out.printf("ID: %d | %s | Price: %.2f | Stock: %d",
                            p.getProductID(), p.getName(), p.getPrice(), p.getStock());
        
        if (p.getBrand() != null || p.getVariant() != null) {
            System.out.print(" | Details: ");

            if (p.getBrand() != null) {
                System.out.print(p.getBrand());
            }
                
            if (p.getVariant() != null) {
                System.out.print(" " + p.getVariant());
            }
        }

        if (p.getExpirationDate() != null) {
            System.out.print(" | Exp: " + p.getExpirationDate());
        }  
        System.out.println();
        }
        System.out.println();
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
}