import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * DataPersistence handles reading and writing user data to files.
 * Manages customer and employee account storage and authentication.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class DataPersistence {
    private static final String DATA_DIR = "data";
    private static final String CUSTOMERS_FILE = DATA_DIR + "/customers.txt";
    private static final String EMPLOYEES_FILE = DATA_DIR + "/employees.txt";
    private static final String DELIMITER = "|||";
    
    /**
     * Constructs a DataPersistence instance and initializes data directory.
     */
    public DataPersistence() {
        initializeDataDirectory();
    }
    
    /**
     * Creates the data directory if it doesn't exist.
     */
    private void initializeDataDirectory() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            
            // Create files if they don't exist
            File customersFile = new File(CUSTOMERS_FILE);
            File employeesFile = new File(EMPLOYEES_FILE);
            
            if (!customersFile.exists()) {
                customersFile.createNewFile();
            }
            if (!employeesFile.exists()) {
                employeesFile.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error initializing data directory: " + e.getMessage());
        }
    }
    
    /**
     * Registers a new customer.
     *
     * @param username the username
     * @param password the password
     * @param name the full name
     * @return true if registration successful, false otherwise
     */
    public boolean registerCustomer(String username, String password, String name) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CUSTOMERS_FILE, true))) {
            // Format: username|||password|||name|||membershipCardNumber|||points
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
     *
     * @param username the username
     * @param password the password
     * @param name the full name
     * @param employeeId the employee ID
     * @return true if registration successful, false otherwise
     */
    public boolean registerEmployee(String username, String password, String name, String employeeId) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EMPLOYEES_FILE, true))) {
            // Format: username|||password|||name|||employeeId
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
     *
     * @param username the username
     * @param password the password
     * @return Customer object if authenticated, null otherwise
     */
    public Customer authenticateCustomer(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 3) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    String name = parts[2];
                    
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        Customer customer = new Customer(name);
                        
                        // Load membership card if exists
                        if (parts.length >= 5 && !parts[3].isEmpty()) {
                            String cardNumber = parts[3];
                            int points = Integer.parseInt(parts[4]);
                            MembershipCard card = new MembershipCard(cardNumber);
                            card.setPoints(points);
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
     *
     * @param username the username
     * @param password the password
     * @return Employee object if authenticated, null otherwise
     */
    public Employee authenticateEmployee(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 4) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    String name = parts[2];
                    String employeeId = parts[3];
                    
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return new Employee(name, employeeId);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error authenticating employee: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Checks if a username already exists.
     *
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        // Check customers
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking customer username: " + e.getMessage());
        }
        
        // Check employees
        try (BufferedReader reader = new BufferedReader(new FileReader(EMPLOYEES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking employee username: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Updates customer data in the file.
     *
     * @param customer the customer to update
     * @param username the customer's username
     */
    public void updateCustomer(Customer customer, String username) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(CUSTOMERS_FILE));
            List<String> updatedLines = new ArrayList<>();
            
            for (String line : lines) {
                String[] parts = line.split("\\|\\|\\|");
                if (parts.length >= 3 && parts[0].equals(username)) {
                    // Update this customer's line
                    String cardNumber = "";
                    String points = "0";
                    
                    if (customer.hasMembershipCard()) {
                        MembershipCard card = customer.getMembershipCard();
                        cardNumber = card.getCardNumber();
                        points = String.valueOf(card.getPoints());
                    }
                    
                    String updatedLine = parts[0] + DELIMITER + parts[1] + DELIMITER + 
                                        customer.getName() + DELIMITER + cardNumber + 
                                        DELIMITER + points;
                    updatedLines.add(updatedLine);
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
     * Saves all transactions to a file.
     *
     * @param transactions list of transactions to save
     */
    public void saveTransactions(List<Transaction> transactions) {
        String filename = DATA_DIR + "/transactions.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            for (Transaction t : transactions) {
                writer.println(t.getTransactionID() + DELIMITER + 
                              t.getCustomer().getName() + DELIMITER + 
                              t.getTotalCost() + DELIMITER + 
                              t.getTimeStamp());
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
}