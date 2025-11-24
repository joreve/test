import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

/**
 * Receipt class generates and displays transaction receipts.
 * Displays itemized purchases, totals, and payment information.
 * Can save receipts to text files for record-keeping.
 *
 */
public class Receipt {
    private Transaction transaction;

    /**
     * Constructs a Receipt for the specified transaction.
     *
     * @param transaction the transaction to create a receipt for
     */
    public Receipt(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Displays the receipt to the console.
     * Shows itemized purchases, subtotal, tax, discounts,
     * total cost, amount received, and change due.
     */
    public void display() {
        String receiptText = generateReceiptText();
        System.out.print(receiptText);
    }

    /**
     * Saves the receipt to a text file.
     * File is named using the transaction ID.
     * Saved in the current directory.
     */
    public void saveToFile() {
        String filename = "receipt_" + transaction.getTransactionID() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            String receiptText = generateReceiptText();
            writer.print(receiptText);
            System.out.println("Receipt saved to: " + filename);
        } catch (IOException e) {
            System.err.println("Error saving receipt to file: " + e.getMessage());
        }
    }

    /**
     * Generates the formatted receipt text.
     * This private helper method creates the receipt content
     * that can be used for both display and file saving.
     *
     * @return the formatted receipt as a String
     */
    private String generateReceiptText() {
        StringBuilder receipt = new StringBuilder();

        receipt.append("\n========================================\n");
        receipt.append("          CONVENIENCE STORE\n");
        receipt.append("             RECEIPT\n");
        receipt.append("========================================\n");

        // Format timestamp
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        receipt.append("Date: ").append(transaction.getTimeStamp().format(formatter)).append("\n");
        receipt.append("Transaction ID: ").append(transaction.getTransactionID()).append("\n");
        receipt.append("Customer: ").append(transaction.getCustomer().getName()).append("\n");

        // Display membership info if applicable
        if (transaction.getCustomer().hasMembershipCard()) {
            MembershipCard card = transaction.getCustomer().getMembershipCard();
            receipt.append("Member Card: ").append(card.getCardNumber()).append("\n");
            receipt.append("Points Balance: ").append(card.getPoints()).append("\n");
        }

        receipt.append("========================================\n");
        receipt.append("ITEMS:\n");
        receipt.append("----------------------------------------\n");

        // Display each item
        double subtotal = 0.0;
        for (CartItem item : transaction.getPurchasedItems()) {
            Product product = item.getProduct();
            double lineTotal = item.computeLineTotal();
            subtotal += lineTotal;

            receipt.append(String.format("%-20s x%-3d  P%8.2f\n",
                    product.getName(),
                    item.getQuantity(),
                    lineTotal));
        }

        receipt.append("----------------------------------------\n");
        receipt.append(String.format("Subtotal:               P%8.2f\n", subtotal));

        // Calculate and display discounts
        double afterDiscount = transaction.applyDiscounts();
        double discount = subtotal - afterDiscount;
        if (discount > 0) {
            receipt.append(String.format("Discount:              -P%8.2f\n", discount));
            receipt.append(String.format("After Discount:         P%8.2f\n", afterDiscount));
        }

        // Calculate and display VAT
        double vat = afterDiscount * 0.12;
        receipt.append(String.format("VAT (12%%):              P%8.2f\n", vat));

        receipt.append("========================================\n");
        receipt.append(String.format("TOTAL:                  P%8.2f\n", transaction.getTotalCost()));
        receipt.append(String.format("Amount Received:        P%8.2f\n",
                transaction.getPayment().getAmountReceived()));
        receipt.append(String.format("Change:                 P%8.2f\n",
                transaction.getPayment().computeChange()));
        receipt.append("========================================\n");
        receipt.append("     Thank you for shopping with us!\n");
        receipt.append("========================================\n\n");

        return receipt.toString();
    }
}