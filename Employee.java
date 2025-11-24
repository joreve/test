import java.util.ArrayList;

/**
 * Represents an employee of the convenience store, inheriting basic user functionality.
 * Employees have specific actions like adding products and restocking.
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
     * Updates the information for an existing product by removing the old version
     * and adding the new, updated version.
     *
     * @param inventory The store's inventory.
     * @param updatedProduct The Product object containing the updated information.
     */
    public void updateProductInfo(Inventory inventory, Product updatedProduct) {
        inventory.removeProduct(updatedProduct.getProductID());
        inventory.addProduct(updatedProduct);

        for (Shelf shelf : inventory.getShelves()) {
            ArrayList<Product> shelfProducts = shelf.getProducts();

            for (int i = 0; i < shelfProducts.size(); i++) {
                if (shelfProducts.get(i).getProductID() == updatedProduct.getProductID()) {
                    shelfProducts.set(i, updatedProduct);
                    break;
                }
            }
        }
        System.out.println("Updated product information for: " + updatedProduct.getName());
    }

    /**
     * Returns the unique employee ID.
     *
     * @return The employee's ID string.
     */
    public String getEmployeeID() {
        return employeeID;
    }
}