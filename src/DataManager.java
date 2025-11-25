import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * DataManager handles all data persistence operations.
 * Manages users, products, and transactions in one unified class.
 *
 * @author Joreve P. De Jesus
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String RECEIPTS_DIR = DATA_DIR + "/receipts";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.txt";
    private static final String EMPLOYEES_FILE = DATA_DIR + "/employees.txt";
    private static final String PRODUCTS_FILE = DATA_DIR + "/products.txt";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.txt";
    private static final String DELIMITER = "|||";
    
    /**
     * Constructs a DataManager and initializes directories and files.
     */
    public DataManager() {
        initializeDataDirectory();
    }
    
    /**
     * Creates necessary directories and files.
     */
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(RECEIPTS_DIR));
            
            createFileIfNotExists(CUSTOMERS_FILE);
            createFileIfNotExists(EMPLOYEES_FILE);
            createFileIfNotExists(PRODUCTS_FILE);
            createFileIfNotExists(TRANSACTIONS_FILE);
            
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
        }
    }
    
    /**
     * Creates a file if it doesn't exist.
     */
    private void createFileIfNotExists(String filepath) throws IOException {
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
    
    /**
     * Registers a new customer.
     */
    public boolean registerCustomer(String username, String password, String name) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            writer.println(username + DELIMITER + password + DELIMITER + name + 
                          DELIMITER + "" + DELIMITER + "0");
            return true;
        } catch (IOException e) {
            System.err.println("Error registering customer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Registers a new employee.
     */
    public boolean registerEmployee(String username, String password, String name, String employeeId) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EMPLOYEES_FILE, true))) {
            writer.println(username + DELIMITER + password + DELIMITER + name + 
                          DELIMITER + employeeId);
            return true;
        } catch (IOException e) {
            System.err.println("Error registering employee: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticates a customer.
     */
    public Customer authenticateCustomer(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 3) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        Customer customer = new Customer(parts[2]);
                        
                        if (parts.length >= 5 && !parts[3].isEmpty()) {
                            MembershipCard card = new MembershipCard(parts[3]);
                            card.setPoints(Integer.parseInt(parts[4]));
                            customer.setMembershipCard(card);
                        }
                        
                        return customer;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error authenticating customer: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Authenticates an employee.
     */
    public Employee authenticateEmployee(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 4) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        return new Employee(parts[2], parts[3]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error authenticating employee: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Checks if username exists.
     */
    public boolean usernameExists(String username) {
        return checkUsernameInFile(CUSTOMERS_FILE, username) || 
               checkUsernameInFile(EMPLOYEES_FILE, username);
    }
    
    private boolean checkUsernameInFile(String filepath, String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Updates customer data.
     */
    public void updateCustomer(Customer customer, String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(CUSTOMERS_FILE));
            List<String> updatedLines = new ArrayList<>();
            
            for (String line : lines) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    String cardNumber = "";
                    String points = "0";
                    
                    if (customer.hasMembershipCard()) {
                        MembershipCard card = customer.getMembershipCard();
                        cardNumber = card.getCardNumber();
                        points = String.valueOf(card.getPoints());
                    }
                    
                    updatedLines.add(parts[0] + DELIMITER + parts[1] + DELIMITER + 
                                   customer.getName() + DELIMITER + cardNumber + 
                                   DELIMITER + points);
                } else {
                    updatedLines.add(line);
                }
            }
            
            Files.write(Paths.get(CUSTOMERS_FILE), updatedLines);
        } catch (IOException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }
    
    /**
     * Loads all products from file.
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
     * Parses a product line.
     */
    private Product parseProductLine(String line) {
        try {
            String[] parts = line.split("\\|\\|\\|");
            if (parts.length < 7) return null;
            
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
            System.err.println("Error parsing product: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Saves all products to file.
     */
    public void saveProducts(List<Product> products) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product product : products) {
                writer.println(formatProductLine(product));
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
        }
    }
    
    /**
     * Formats a product line.
     */
    private String formatProductLine(Product product) {
        return product.getProductID() + DELIMITER +
               product.getName() + DELIMITER +
               product.getPrice() + DELIMITER +
               product.getStock() + DELIMITER +
               product.getCategory().getName() + DELIMITER +
               product.getCategory().getType() + DELIMITER +
               (product.getBrand() != null ? product.getBrand() : "") + DELIMITER +
               (product.getVariant() != null ? product.getVariant() : "") + DELIMITER +
               (product.getExpirationDate() != null ? product.getExpirationDate().toString() : "");
    }
    
    /**
     * Adds a product.
     */
    public void addProduct(Product product) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PRODUCTS_FILE, true))) {
            writer.println(formatProductLine(product));
        } catch (IOException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }
    
    /**
     * Updates a product.
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
     * Removes a product.
     */
    public void removeProduct(int productID) {
        List<Product> products = loadProducts();
        products.removeIf(p -> p.getProductID() == productID);
        saveProducts(products);
    }
    
    /**
     * Checks if product exists.
     */
    public boolean productExists(int productID) {
        return loadProducts().stream().anyMatch(p -> p.getProductID() == productID);
    }
    
    /**
     * Saves a transaction to sales history.
     */
    public void saveTransaction(Transaction transaction) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            writer.println(transaction.getTransactionID() + DELIMITER +
                          transaction.getCustomer().getName() + DELIMITER +
                          transaction.getTotalCost() + DELIMITER +
                          transaction.getTimeStamp().format(formatter));
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }
    
    /**
     * Loads all transactions.
     */
    public List<String> loadTransactions() {
        List<String> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                transactions.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    /**
     * Saves receipt to file in receipts folder.
     */
    public void saveReceipt(String transactionID, String receiptContent) {
        String filename = RECEIPTS_DIR + "/receipt_" + transactionID + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print(receiptContent);
            System.out.println("Receipt saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
        }
    }
}