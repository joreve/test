/**
 * Represents a category for products, composed of a main category (e.g., Food, Beverages)
 * and a more specific sub-category (e.g., Vegetable, Juice).
 * 
 * @author Joreve P. De Jesus
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

    public String getName() { 
        return mainCategory; 
    }

    public String getType() { 
        return subCategory; 
    }

    public String setName(String mainCategory) { 
        return this.mainCategory = mainCategory; 
    }

    public String setType(String subCategory) { 
        return this.subCategory = subCategory; 
    }
}