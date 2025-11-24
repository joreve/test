/******************************************************************************
 *  Description     : Main class to demonstrate the functionality of the Convenience Store Management System.
 *  Author/s        : De Jesus, Joreve P., Pelagio, Dana Ysabelle A.
 *  Section         : S12
 *  Last Modified   : October 24, 2025
 ******************************************************************************/
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The main driver class for the Convenience Store Management System application.
 * Contains the main method to initialize objects, perform operations, and display results.
 */
public class Main {
    /**
     * The entry point of the application.
     */
    public static void main(String[] args) {
        System.out.println("========================================\n");
        System.out.println("CONVENIENCE STORE MANAGEMENT SYSTEM\n");
        System.out.println("========================================\n");

        // 1. Create Convenience Store
        ConvenienceStore store = new ConvenienceStore("11-Seven", "Taft");
        System.out.println("Store Created: " + store.getName());
        System.out.println("Location: " + store.getLocation());
        System.out.println();

        // 2. Create Categories
        Category vegetable = new Category("Food", "Vegetable");
        Category fruits = new Category("Food", "Fruits");
        Category snacks = new Category("Food", "Snacks");
        Category juice = new Category("Beverages", "Juice");

        // 3. Create Shelves
        Shelf vegetableShelf = new Shelf(vegetable);
        Shelf fruitsShelf = new Shelf(fruits);
        Shelf snacksShelf = new Shelf(snacks);
        Shelf juiceShelf = new Shelf(juice);

        store.getInventory().addShelf(vegetableShelf);
        store.getInventory().addShelf(fruitsShelf);
        store.getInventory().addShelf(snacksShelf);
        store.getInventory().addShelf(juiceShelf);

        System.out.println("Shelves created and added to inventory");

        // 4. Create Products
        // Products with general attributes only (no brand, variant, or expiration)
        Product carrot = new Product(101, "Carrot", 20.00, 30, vegetable);
        Product apple = new Product(201, "Apple", 50.00, 40, fruits);
        
        // Products with brand and variant only
        Product chips = new Product(301, "Potato Chips", 35.00, 30, snacks, "Lay's", "Classic", null);
        Product cookies = new Product(302, "Cookies", 35.00, 25, snacks, "Oreo", "Original", null);
        
        // Products with expiration date only
        Product broccoli = new Product(102, "Broccoli", 60.00, 20, vegetable, null, null, LocalDate.of(2025, 10, 27));
        Product banana = new Product(202, "Banana", 40.00, 50, fruits, null, null, LocalDate.of(2025, 10, 29));
        
        // Products with brand, variant, AND expiration date
        Product orangejuice = new Product(401, "Orange Juice", 90.00, 35, juice, "Minute Maid", "1L", LocalDate.of(2025, 11, 3));
        Product applejuice = new Product(402, "Apple Juice", 20.00, 35, juice, "Zest-O", "200mL", LocalDate.of(2025, 11, 3));

        // 5. Create Employee and Add Products
        Employee employee = new Employee("Joreve De Jesus", "EMP12410179");
        System.out.println("\n========================================\n");
        System.out.println("EMPLOYEE: " + employee.getName() + " (ID: " + employee.getEmployeeID() + ")\n");
        System.out.println("========================================");

        System.out.println("--- Adding Products to Inventory ---");
        employee.addProduct(store.getInventory(), carrot);
        employee.addProduct(store.getInventory(), apple);
        employee.addProduct(store.getInventory(), chips);
        employee.addProduct(store.getInventory(), cookies);
        employee.addProduct(store.getInventory(), broccoli);
        employee.addProduct(store.getInventory(), banana);
        employee.addProduct(store.getInventory(), orangejuice);
        employee.addProduct(store.getInventory(), applejuice);
        System.out.println();

        // 6. Add Products to Shelves
        System.out.println("--- Organizing Products on Shelves ---");
        vegetableShelf.addProduct(carrot);
        fruitsShelf.addProduct(apple);
        snacksShelf.addProduct(chips);
        snacksShelf.addProduct(cookies);
        vegetableShelf.addProduct(broccoli);
        fruitsShelf.addProduct(banana);
        juiceShelf.addProduct(orangejuice);
        juiceShelf.addProduct(applejuice);
        System.out.println("All products organized on respective shelves\n");

        // 7. Display Initial Inventory
        System.out.println("========================================\n");
        System.out.println("INITIAL INVENTORY\n");
        System.out.println("========================================\n");
        store.displayInventory();

        // 8. Test Restocking
        System.out.println("========================================\n");
        System.out.println("RESTOCKING\n");
        System.out.println("========================================\n");
        employee.restockItem(store.getInventory(), banana, 30);
        employee.restockItem(store.getInventory(), orangejuice, 20);
        System.out.println();

        // 9. Display Inventory After Restock
        System.out.println("========================================\n");
        System.out.println("INVENTORY AFTER RESTOCK\n");
        System.out.println("========================================\n");
        store.displayInventory();

        // 10. Create Customer WITHOUT Membership
        System.out.println("========================================\n");
        System.out.println("CUSTOMER PURCHASE - NO MEMBERSHIP\n");
        System.out.println("========================================\n");

        Customer customer1 = new Customer("Juan Dela Cruz");
        System.out.println("Customer: " + customer1.getName());
        System.out.println("Has Membership: " + customer1.hasMembershipCard());
        System.out.println();

        // 11. Customer adds items to cart
        System.out.println("--- Adding Items to Cart ---");
        customer1.addToCart(carrot, 5);
        customer1.addToCart(apple, 3);
        customer1.addToCart(chips, 2);
        System.out.println();

        // 12. View cart and running total
        System.out.println("--- Cart Contents ---");
        customer1.getCart().listItems();
        System.out.printf("Running Total: P%.2f\n", customer1.viewRunningTotal());
        System.out.println();

        // 13. Checkout Process
        System.out.println("========================================\n");
        System.out.println("CHECKOUT PROCESS\n");
        System.out.println("========================================\n");

        Transaction transaction1 = customer1.checkOut(store);
        
        // 14. Process Payment
        System.out.println("--- Payment ---");
        double totalWithVAT = transaction1.calculateTotalWithTax();
        System.out.printf("Total with VAT: P%.2f\n", totalWithVAT);

        Payment payment1 = new Payment(500.00, totalWithVAT);
        transaction1.setPayment(payment1);
        System.out.printf("Amount Received: P%.2f\n", 500.00);
        System.out.println("Payment Sufficient? " + payment1.isSufficient());
        System.out.printf("Change: P%.2f\n\n", payment1.computeChange());

        Receipt receipt1 = transaction1.generateReceipt();
        
        // 15. Display Receipt
        System.out.println("--- RECEIPT (Customer Copy) ---");
        receipt1.display();

        // 16. Save receipt to file
        receipt1.saveToFile();
        System.out.println("Receipt saved to file\n");

        // 17. Create Customer WITH Membership
        System.out.println("========================================\n");
        System.out.println("CUSTOMER PURCHASE - WITH MEMBERSHIP\n");
        System.out.println("========================================\n");

        MembershipCard card = new MembershipCard("CARD-12345");
        Customer customer2 = new Customer("Maria Santos", card);
        System.out.println("Customer: " + customer2.getName());
        System.out.println("Card Number: " + card.getCardNumber());
        System.out.println("Initial Points: " + card.getPoints());
        System.out.println();

        // 18. Customer 2 shops
        System.out.println("--- Adding Items to Cart ---");
        customer2.addToCart(orangejuice, 2);
        customer2.addToCart(banana, 4);
        customer2.addToCart(cookies, 3);

        // 19. View cart
        customer2.getCart().listItems();
        System.out.printf("\nRunning Total: P%.2f\n", customer2.viewRunningTotal());
        System.out.println();

        // 20. Checkout with membership points
        System.out.println("========================================\n");
        System.out.println("CHECKOUT WITH MEMBERSHIP\n");
        System.out.println("========================================\n");

        Transaction transaction2 = customer2.checkOut(store);
        double subtotal = transaction2.calculateTotalWithTax();

        card.addPoints(subtotal);
        System.out.printf("Points earned from P%.2f purchase: %d\n",
                subtotal, (int)(subtotal / 50));
        System.out.println("Total Points: " + card.getPoints());

        double discount = card.getDiscount();
        double finalTotal = subtotal - discount;
        System.out.printf("Membership Discount: -P%.2f\n", discount);
        System.out.printf("Final Total: P%.2f\n\n", finalTotal);

        Payment payment2 = new Payment(500.00, finalTotal);
        transaction2.setPayment(payment2);

        Receipt receipt2 = transaction2.generateReceipt();
        receipt2.display();
        receipt2.saveToFile();

        // 21. Test Senior Discount
        System.out.println("\n========================================\n");
        System.out.println("SENIOR CITIZEN DISCOUNT\n");
        System.out.println("========================================\n");

        double regularPrice = 500.00;
        double seniorPrice = DiscountPolicy.applySeniorDiscount(regularPrice);
        System.out.printf("Regular Price: P%.2f\n", regularPrice);
        System.out.printf("Senior Price (20%% off): P%.2f\n", seniorPrice);
        System.out.printf("Savings: P%.2f\n", regularPrice - seniorPrice);

        // 22. Sales History
        System.out.println("\n========================================\n");
        System.out.println("SALES HISTORY\n");
        System.out.println("========================================\n");
        store.displaySalesHistory();

        // 23. Display Updated Inventory (after purchases)
        System.out.println("\n========================================\n");
        System.out.println("FINAL INVENTORY (After Purchases)\n");
        System.out.println("========================================\n");
        store.displayInventory();

        // 24. Update Product Information
        System.out.println("\n========================================\n");
        System.out.println("TESTING UPDATE PRODUCT INFO\n");
        System.out.println("========================================\n");

        System.out.println("--- Before Update ---");
        System.out.println("Carrot - Price: P" + carrot.getPrice() + " Stock: " + carrot.getStock());
        
        Product updatedCarrot = new Product(101, "Carrot (Premium)", 30.00, carrot.getStock(), vegetable);
        employee.updateProductInfo(store.getInventory(), updatedCarrot);
        
        System.out.println("\n--- After Update ---");
        store.displayInventory();

        // 25. Test flagLowStock
        System.out.println("========================================\n");
        System.out.println("CHECKING LOW STOCK ITEMS\n");
        System.out.println("========================================\n");

        ArrayList<Product> lowStockItems = store.getInventory().flagLowStock();
        
        if (lowStockItems.isEmpty()) {
            System.out.println("No low stock items found.\n");
        } 
        else {
            System.out.println("Low Stock Alert! The following items have less than 5 units:\n");
            for (Product p : lowStockItems) {
                System.out.println("- " + p.getName() + " (ID: " + p.getProductID() + 
                                    ") - Only " + p.getStock() + " units remaining");
            }
            System.out.println("\nRecommendation: Restock these items soon!\n");
        }

        System.out.println("--- Adding More Low Stock Items ---");
        Product onion = new Product(103, "Onion", 20.00, 2, vegetable);
        Product grape = new Product(203, "Grape", 50.00, 4, fruits);

        employee.addProduct(store.getInventory(), onion);
        employee.addProduct(store.getInventory(), grape);

        vegetableShelf.addProduct(onion);
        fruitsShelf.addProduct(grape);

        System.out.println("\n--- Checking Low Stock Again ---\n");
        lowStockItems = store.getInventory().flagLowStock();
        System.out.println();

        if (lowStockItems.isEmpty()) {
            System.out.println("No low stock items found.\n");
        } 
        else {
            System.out.println("Low Stock Alert! The following items have less than 5 units:\n");
            for (Product p : lowStockItems) {
                System.out.println("- " + p.getName() + " (ID: " + p.getProductID() + 
                                    ") - Only " + p.getStock() + " units remaining");
            }
            System.out.println("\nRecommendation: Restock these items soon!\n");
        }
    }
}


 /**
 * This is to certify that this project is my/our own work, based on my/our personal
 * efforts in studying and applying the concepts learned. I/We have constructed the
 * functions and their respective algorithms and corresponding code by myself/ourselves.
 * The program was run, tested, and debugged by my/our own efforts. I/We further certify
 * that I/we have not copied in part or whole or otherwise plagiarized the work of
 * other students and/or persons.
 *
 * (De Jesus, Joreve P.) (DLSU ID# 12410179)
 * (Pelagio, Dana Ysabelle A.) (DLSU ID# 12214973)
 */
