import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * ProductDataManager handles reading and writing product data to files.
 * Manages product storage and retrieval.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class ProductDataManager {
    private static final String DATA_DIR = "data";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.txt";
    private static final String DELIMITER = "|||";
    
    /**
     * Constructs a ProductDataManager instance and initializes data directory.
     */
    public ProductDataManager() {
        initializeDataDirectory();
    }
    
    /**
     * Creates the data directory and products file if they don't exist.
     */
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            
            File productsFile = new File(PRODUCTS_FILE);
            if (!productsFile.exists()) {
                productsFile.createNewFile();
                createDummyProducts();
            }
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
        }
    }
    
    /**
     * Creates dummy products for initial data.
     */
    private void createDummyProducts() {
        List<String> dummyProducts = Arrays.asList(
            "301|||Potato Chips|||35.00|||30|||Food|||Snacks|||Lay's|||Classic|||",
            "302|||Cookies|||35.00|||25|||Food|||Snacks|||Oreo|||Original|||",
            "401|||Soda|||20.00|||40|||Beverages|||Cold Drinks|||Coke|||Regular|||",
            "402|||Orange Juice|||45.00|||35|||Beverages|||Cold Drinks|||Minute Maid|||1L|||",
            "101|||Carrot|||20.00|||30|||Food|||Vegetables||||||",
            "102|||Broccoli|||60.00|||20|||Food|||Vegetables||||||2025-11-28",
            "201|||Apple|||50.00|||40|||Food|||Fruits||||||",
            "202|||Banana|||40.00|||50|||Food|||Fruits||||||2025-11-30"
        );
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (String product : dummyProducts) {
                writer.println(product);
            }
            System.out.println("Dummy products created successfully.");
        } catch (IOException e) {
            System.err.println("Error creating dummy products: " + e.getMessage());
        }
    }
    
    /**
     * Loads all products from file.
     *
     * @return List of products
     */
    public List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PRODUCTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProductLine(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading products: " + e.getMessage());
        }
        
        return products;
    }
    
    /**
     * Parses a line from the products file into a Product object.
     *
     * @param line The line to parse
     * @return Product object or null if parsing fails
     */
    private Product parseProductLine(String line) {
        try {
            String[] parts = line.split("\\|\\|\\|");
            
            if (parts.length < 7) {
                return null;
            }
            
            int productID = Integer.parseInt(parts[0]);
            String name = parts[1];
            double price = Double.parseDouble(parts[2]);
            int stock = Integer.parseInt(parts[3]);
            String mainCategory = parts[4];
            String subCategory = parts[5];
            
            Category category = new Category(mainCategory, subCategory);
            
            String brand = parts.length > 6 && !parts[6].isEmpty() ? parts[6] : null;
            String variant = parts.length > 7 && !parts[7].isEmpty() ? parts[7] : null;
            LocalDate expirationDate = null;
            
            if (parts.length > 8 && !parts[8].isEmpty()) {
                expirationDate = LocalDate.parse(parts[8]);
            }
            
            return new Product(productID, name, price, stock, category, brand, variant, expirationDate);
            
        } catch (Exception e) {
            System.err.println("Error parsing product line: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Saves all products to file.
     *
     * @param products List of products to save
     */
    public void saveProducts(List<Product> products) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products) {
                String line = formatProductLine(product);
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }
    
    /**
     * Formats a Product object into a line for the file.
     *
     * @param product The product to format
     * @return Formatted string
     */
    private String formatProductLine(Product product) {
        StringBuilder line = new StringBuilder();
        
        line.append(product.getProductID()).append(DELIMITER);
        line.append(product.getName()).append(DELIMITER);
        line.append(product.getPrice()).append(DELIMITER);
        line.append(product.getStock()).append(DELIMITER);
        line.append(product.getCategory().getName()).append(DELIMITER);
        line.append(product.getCategory().getType()).append(DELIMITER);
        line.append(product.getBrand() != null ? product.getBrand() : "").append(DELIMITER);
        line.append(product.getVariant() != null ? product.getVariant() : "").append(DELIMITER);
        line.append(product.getExpirationDate() != null ? product.getExpirationDate().toString() : "");
        
        return line.toString();
    }
    
    /**
     * Adds a new product to the file.
     *
     * @param product The product to add
     */
    public void addProduct(Product product) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE, true))) {
            String line = formatProductLine(product);
            writer.println(line);
        } catch (IOException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }
    
    /**
     * Updates a product in the file.
     *
     * @param updatedProduct The product with updated information
     */
    public void updateProduct(Product updatedProduct) {
        List<Product> products = loadProducts();
        
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getProductID() == updatedProduct.getProductID()) {
                products.set(i, updatedProduct);
                break;
            }
        }
        
        saveProducts(products);
    }
    
    /**
     * Removes a product from the file.
     *
     * @param productID The ID of the product to remove
     */
    public void removeProduct(int productID) {
        List<Product> products = loadProducts();
        products.removeIf(p -> p.getProductID() == productID);
        saveProducts(products);
    }
    
    /**
     * Checks if a product ID already exists.
     *
     * @param productID The product ID to check
     * @return true if exists, false otherwise
     */
    public boolean productExists(int productID) {
        List<Product> products = loadProducts();
        return products.stream().anyMatch(p -> p.getProductID() == productID);
    }
}