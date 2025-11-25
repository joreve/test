import java.util.ArrayList;

/**
 * Represents an employee of the convenience store, inheriting basic user functionality.
 * Employees have specific actions like adding products and restocking.
 * 
 * @author Joreve P. De Jesus
 */
public class Employee extends User {
    private String employeeID;

    /**
     * Constructs a new Employee with a name and an employee ID.
     *
     * @param name The name of the employee.
     * @param employeeID The unique ID of the employee.
     */
    public Employee(String name, String employeeID) {
        super(name);
        this.employeeID = employeeID;
    }

    /**
     * Adds a new product to the store's inventory.
     *
     * @param inventory The store's inventory where the product will be added.
     * @param product The product to be added.
     */
    public void addProduct(Inventory inventory, Product product) {
        inventory.addProduct(product);
        System.out.println("Product added: " + product.getName());
    }

    /**
     * Increases the stock of an existing product in the inventory.
     *
     * @param inventory The store's inventory.
     * @param product The product to be restocked.
     * @param quantity The amount to add to the product's current stock.
     */
    public void restockItem(Inventory inventory, Product product, int quantity) {
        product.restock(quantity);
        System.out.println("Restocked " + product.getName() + " by " + quantity + " units.");
    }

    /**
     * Updates the information for an existing product.
     * If category changed, removes from old shelf and adds to new shelf.
     *
     * @param inventory The store's inventory.
     * @param updatedProduct The Product object containing the updated information.
     */
    public void updateProductInfo(Inventory inventory, Product updatedProduct) {
        // Find the old product to check if category changed
        Product oldProduct = null;
        for (Product p : inventory.getProducts()) {
            if (p.getProductID() == updatedProduct.getProductID()) {
                oldProduct = p;
                break;
            }
        }
        
        // Remove old product from inventory and all shelves
        inventory.removeProduct(updatedProduct.getProductID());
        
        // Add updated product to inventory
        inventory.addProduct(updatedProduct);
        
        // Find or create appropriate shelf for the updated product
        boolean shelfFound = false;
        for (Shelf shelf : inventory.getShelves()) {
            if (shelf.getCategory().getName().equals(updatedProduct.getCategory().getName()) &&
                shelf.getCategory().getType().equals(updatedProduct.getCategory().getType())) {
                shelf.addProduct(updatedProduct);
                shelfFound = true;
                break;
            }
        }
        
        // If no matching shelf exists, create a new one
        if (!shelfFound) {
            Shelf newShelf = new Shelf(updatedProduct.getCategory());
            newShelf.addProduct(updatedProduct);
            inventory.addShelf(newShelf);
            System.out.println("Created new shelf for category: " + 
                             updatedProduct.getCategory().getName() + " - " + 
                             updatedProduct.getCategory().getType());
        }
        
        System.out.println("Updated product information for: " + updatedProduct.getName());
    }
    
    public String getEmployeeID() {
        return employeeID;
    }
}