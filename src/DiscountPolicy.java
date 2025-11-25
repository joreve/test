/**
 * DiscountPolicy class provides static methods for applying
 * various types of discounts to purchases.
 * Includes membership discounts, senior citizen discounts,
 * and VAT calculations.
 * 
 * @author Dana Ysabelle A. Pelagio
 */
public class DiscountPolicy {

    private static final double SENIOR_DISCOUNT_RATE = 0.20; // 20% senior discount
    private static final double VAT_RATE = 0.12; // 12% VAT

    /**
     * Applies membership discount based on available points.
     * Points are used if they provide a discount.
     * Does not automatically redeem points - just calculates potential discount.
     *
     * @param customer the customer with membership card
     * @param total the total amount before discount
     * @return the total after membership discount is applied
     */
    public static double applyMembershipDiscount(Customer customer, double total) {
        if (customer == null || !customer.hasMembershipCard()) {
            return total;
        }

        MembershipCard card = customer.getMembershipCard();
        double availableDiscount = card.getDiscount();

        // Apply discount but ensure total doesn't go below 0
        if (availableDiscount > 0) {
            double discountedTotal = total - availableDiscount;
            return Math.max(discountedTotal, 0);
        }

        return total;
    }

    /**
     * Applies senior citizen discount to the total.
     * Senior citizens get 20% discount on their purchase.
     *
     * @param total the total amount before discount
     * @return the total after senior discount is applied
     */
    public static double applySeniorDiscount(double total) {
        if (total <= 0) {
            return total;
        }
        double discount = total * SENIOR_DISCOUNT_RATE;
        return total - discount;
    }

    /**
     * Calculates the VAT (Value Added Tax) amount for a given subtotal.
     * VAT rate is 12%.
     *
     * @param subtotal the subtotal amount before tax
     * @return the VAT amount
     */
    public static double calculateVAT(double subtotal) {
        if (subtotal <= 0) {
            return 0.0;
        }

        return subtotal * VAT_RATE;
    }

    /**
     * Gets the current senior discount rate.
     *
     * @return the senior discount rate (0.20 = 20%)
     */
    public static double getSeniorDiscountRate() {
        return SENIOR_DISCOUNT_RATE;
    }

    /**
     * Gets the current VAT rate.
     *
     * @return the VAT rate (0.12 = 12%)
     */
    public static double getVATRate() {
        return VAT_RATE;
    }
}