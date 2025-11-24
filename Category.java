/**
 * Represents a category for products, composed of a main category (e.g., Food, Beverages)
 * and a more specific sub-category (e.g., Vegetable, Juice).
 */
public class Category {
    private String mainCategory;
    private String subCategory;

    /**
     * Constructs a new Category with the specified main and sub-categories.
     *
     * @param mainCategory The category name.
     * @param subCategory The specific type within the main category.
     */
    public Category(String mainCategory, String subCategory) {
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }

    /**
     * Returns the main category name.
     *
     * @return The main category (e.g., "Food").
     */
    public String getName() { 
        return mainCategory; 
    }

    /**
     * Returns the sub-category type.
     *
     * @return The sub-category (e.g., "Vegetable").
     */
    public String getType() { 
        return subCategory; 
    }
}