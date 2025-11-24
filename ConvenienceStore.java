import java.util.ArrayList;

/**
 * Represents the main convenience store entity.
 * It holds the store's name, location, and manages its inventory.
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
     * Displays the current state of the store's inventory, organized by shelf.
     */
    public void displayInventory() {
        inventory.displayInventory();
    }

    /**
     * Returns the store's inventory object.
     *
     * @return The Inventory instance.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Returns the name of the store.
     *
     * @return The store's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the location of the store.
     *
     * @return The store's location.
     */
    public String getLocation() {
        return location;
    }
}