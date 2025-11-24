import java.util.ArrayList;

/**
 * Manages the store's product inventory, keeping track of all products and organizing them into shelves.
 */
class Inventory {
    private ArrayList<Product> products;
    private ArrayList<Shelf> shelves;

    /**
     * Constructs a new, empty Inventory with initialized lists for products and shelves.
     */
    public Inventory() {
        this.products = new ArrayList<>();
        this.shelves = new ArrayList<>();
    }

    /**
     * Adds a new shelf to the inventory's list of shelves.
     *
     * @param shelf The shelf to be added.
     */
    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
    }

    /**
     * Returns the list of shelves in the inventory.
     *
     * @return The ArrayList of shelves.
     */
    public ArrayList<Shelf> getShelves() {
        return shelves;
    }

    /**
     * Adds a new product to the master product list.
     *
     * @param product The product to be added.
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    /**
     * Removes a product from the master product list based on its ID.
     *
     * @param productID The ID of the product to be removed.
     */
    public void removeProduct(int productID) {
        products.removeIf(p -> p.getProductID() == productID);
    }

    /**
     * Restocks a product by finding it by ID and calling its restock method.
     *
     * @param productID The ID of the product to restock.
     * @param quantity The amount to add to the stock.
     */
    public void restock(int productID, int quantity) {
        for (Product p : products) {
            if (p.getProductID() == productID) {
                p.restock(quantity);
                break;
            }
        }
    }

    /**
     * Checks all products and returns a list of those with a stock level less than 5.
     *
     * @return An ArrayList of products considered low in stock.
     */
    public ArrayList<Product> flagLowStock() {
        ArrayList<Product> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getStock() < 5) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    /**
     * Automatically reduces stock for all items in the cart after purchase.
     *
     * @param cart the shopping cart containing items to reduce from inventory
     */
    public void autoReduceStock(Cart cart) {
        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int quantityPurchased = item.getQuantity();

            // Find the product in inventory and reduce its stock
            for (Product p : products) {
                if (p.getProductID() == product.getProductID()) {
                    p.reduceStock(quantityPurchased);
                    break;
                }
            }
        }
    }

    /**
     * Displays the current inventory by iterating through all shelves and calling their {@code displayShelf} method.
     */
    public void displayInventory() {
        for (Shelf s : shelves) {
            s.displayShelf();
        }
    }

    /**
     * Returns the master list of all products.
     *
     * @return The ArrayList of all products.
     */
    public ArrayList<Product> getProducts() {
        return products;
    }
}