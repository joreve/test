import java.util.ArrayList;

/**
 * Represents the main convenience store entity.
 * It holds the store's name, location, and manages its inventory.
 * 
 * @author Joreve P. De Jesus
 */
public class ConvenienceStore {
    private String name;
    private String location;
    private Inventory inventory;
    private ArrayList<Transaction> salesHistory;

    /**
     * Constructs a new ConvenienceStore with a name and location, and initializes an empty inventory.
     *
     * @param name The name of the store.
     * @param location The location of the store.
     */
    public ConvenienceStore(String name, String location) {
        this.name = name;
        this.location = location;
        this.inventory = new Inventory();
        this.salesHistory = new ArrayList<>();
    }

    /**
     * Saves each transaction into an ArrayList of sales history.
     */
    public void saveToSalesHistory(Transaction transaction) {
        salesHistory.add(transaction);
    }

    /**
     * Displays the current sales history.
     */
    public void displaySalesHistory() {
        for(Transaction sales : salesHistory) {
            System.out.println("Transaction ID: " + sales.getTransactionID());
        }
    }

    /**
     * Returns the sales history list.
     *
     * @return The ArrayList of transactions.
     */
    public ArrayList<Transaction> getSalesHistory() {
        return salesHistory;
    }

    /**
     * Displays the current state of the store's inventory, organized by shelf.
     */
    public void displayInventory() {
        inventory.displayInventory();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name) {
        this.name = name;
    }

    void setLocation(String location) {
        this.location = location;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}