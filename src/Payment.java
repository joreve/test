/**
 * Payment class handles payment information for a transaction.
 * Stores the amount received from customer and the total cost.
 * Computes change and validates if payment is sufficient.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class Payment {
    private double amountReceived;
    private double totalCost;

    /**
     * Constructs a Payment with the specified amount received and total cost.
     *
     * @param amountReceived the amount of money received from the customer
     * @param totalCost the total cost of the purchase
     */
    public Payment(double amountReceived, double totalCost) {
        this.amountReceived = amountReceived;
        this.totalCost = totalCost;
    }

    /**
     * Computes the change to be given to the customer.
     *
     * @return the change amount, or 0 if payment is insufficient
     */
    public double computeChange() {
        if (amountReceived >= totalCost) {
            return amountReceived - totalCost;
        }
        return 0.0;
    }

    /**
     * Checks if the payment received is sufficient to cover the total cost.
     *
     * @return true if payment is sufficient, false otherwise
     */
    public boolean isSufficient() {
        return amountReceived >= totalCost;
    }

    public double getAmountReceived() {
        return amountReceived;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setAmountReceived(double amountReceived) {
        if (amountReceived >= 0) {
            this.amountReceived = amountReceived;
        }
    }

    public void setTotalCost(double totalCost) {
        if (totalCost >= 0) {
            this.totalCost = totalCost;
        }
    }
}