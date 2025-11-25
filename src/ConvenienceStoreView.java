import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * ConvenienceStoreView displays the main shopping interface organized by categories.
 * Shows products in nested tabs (Food/Beverages > Subcategories).
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class ConvenienceStoreView extends BorderPane {
    private ConvenienceStore store;
    private Customer customer;
    private ConvenienceStoreController controller;
    private MainApplication mainApp;
    
    private TabPane mainCategoryTabs;
    private Button viewCartButton;
    private Label cartItemCountLabel;
    
    /**
     * Constructs a ConvenienceStoreView.
     *
     * @param store The convenience store
     * @param customer The current customer
     * @param controller The store controller
     */
    public ConvenienceStoreView(ConvenienceStore store, Customer customer, ConvenienceStoreController controller, MainApplication mainApp) {
        this.store = store;
        this.customer = customer;
        this.controller = controller;
        this.mainApp = mainApp;
        initializeUI();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        // Top bar
        HBox topBar = createTopBar();
        setTop(topBar);
        
        // Center: Main category tabs (Food, Beverages)
        mainCategoryTabs = new TabPane();
        mainCategoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Organize products by main category then subcategory
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        // Create main tabs
        for (Map.Entry<String, Map<String, List<Product>>> entry : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(entry.getKey(), entry.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        setCenter(mainCategoryTabs);
        
        // Bottom: Shopping info
        HBox bottomBar = createBottomBar();
        setBottom(bottomBar);
    }
    
    /**
     * Creates the top bar with store info and customer details.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #4CAF50;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label storeLabel = new Label("🏪 " + store.getName() + " - " + store.getLocation());
        storeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        storeLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label customerLabel = new Label("👤 " + customer.getName());
        customerLabel.setFont(Font.font("Arial", 16));
        customerLabel.setStyle("-fx-text-fill: white;");
        
        VBox customerBox = new VBox(5);
        customerBox.setAlignment(Pos.CENTER_RIGHT);
        customerBox.getChildren().add(customerLabel);
        
        if (customer.hasMembershipCard()) {
            Label memberLabel = new Label("⭐ " + customer.getMembershipCard().getPoints() + " points");
            memberLabel.setFont(Font.font("Arial", 12));
            memberLabel.setStyle("-fx-text-fill: #FFD700;");
            customerBox.getChildren().add(memberLabel);
        }
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        logoutButton.setOnAction(e -> controller.handleLogout());
        
        topBar.getChildren().addAll(storeLabel, spacer, customerBox, logoutButton);
        return topBar;
    }
    
    /**
     * Creates the bottom bar with cart button.
     */
    private HBox createBottomBar() {
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        
        cartItemCountLabel = new Label("Cart: 0 items");
        cartItemCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        viewCartButton = new Button("🛒 View Cart");
        viewCartButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        viewCartButton.setPrefWidth(150);
        viewCartButton.setPrefHeight(45);
        viewCartButton.setOnAction(e -> controller.showCartView());
        
        bottomBar.getChildren().addAll(cartItemCountLabel, viewCartButton);
        return bottomBar;
    }
    
    /**
     * Organizes products by main category (Food, Beverages) then subcategory.
     */
    private Map<String, Map<String, List<Product>>> organizeProductsByCategory() {
        Map<String, Map<String, List<Product>>> organized = new LinkedHashMap<>();
        
        for (Shelf shelf : store.getInventory().getShelves()) {
            String mainCategory = shelf.getCategory().getName();
            String subCategory = shelf.getCategory().getType();
            
            organized.putIfAbsent(mainCategory, new LinkedHashMap<>());
            organized.get(mainCategory).putIfAbsent(subCategory, new ArrayList<>());
            organized.get(mainCategory).get(subCategory).addAll(shelf.getProducts());
        }
        
        return organized;
    }
    
    /**
     * Creates a main category tab (Food or Beverages) with sub-category tabs inside.
     */
    private Tab createMainCategoryTab(String mainCategory, Map<String, List<Product>> subCategories) {
        Tab mainTab = new Tab(mainCategory);
        
        // Create sub-tabs for each subcategory
        TabPane subTabPane = new TabPane();
        subTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        for (Map.Entry<String, List<Product>> subCat : subCategories.entrySet()) {
            Tab subTab = createSubCategoryTab(subCat.getKey(), subCat.getValue());
            subTabPane.getTabs().add(subTab);
        }
        
        mainTab.setContent(subTabPane);
        return mainTab;
    }
    
    /**
     * Creates a sub-category tab with products displayed in a flow pane.
     */
    private Tab createSubCategoryTab(String subCategory, List<Product> products) {
        Tab tab = new Tab(subCategory);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        
        FlowPane productFlow = new FlowPane();
        productFlow.setHgap(20);
        productFlow.setVgap(20);
        productFlow.setPadding(new Insets(20));
        productFlow.setAlignment(Pos.TOP_LEFT);
        
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productFlow.getChildren().add(productCard);
        }
        
        scrollPane.setContent(productFlow);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates a product card with details and add to cart button.
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 8; " +
                     "-fx-background-color: white; -fx-background-radius: 8; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(220);
        card.setAlignment(Pos.TOP_LEFT);
        
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(190);
        
        if (product.getBrand() != null || product.getVariant() != null) {
            String details = "";
            if (product.getBrand() != null) details += product.getBrand();
            if (product.getVariant() != null) {
                if (!details.isEmpty()) details += " ";
                details += product.getVariant();
            }
            
            Label detailsLabel = new Label(details);
            detailsLabel.setFont(Font.font("Arial", 12));
            detailsLabel.setStyle("-fx-text-fill: #666;");
            card.getChildren().add(detailsLabel);
        }
        
        if (product.getExpirationDate() != null) {
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
            Label expLabel = new Label("Exp: " + product.getExpirationDate());
            expLabel.setFont(Font.font("Arial", 11));
            
            if (daysUntilExpiry <= 15) {
                expLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            } else {
                expLabel.setStyle("-fx-text-fill: #ff6b6b;");
            }
            card.getChildren().add(expLabel);
        }
        
        Separator sep = new Separator();
        
        Label priceLabel = new Label(String.format("₱%.2f", product.getPrice()));
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        priceLabel.setStyle("-fx-text-fill: #4CAF50;");
        
        Label stockLabel = new Label("Stock: " + product.getStock());
        stockLabel.setFont(Font.font("Arial", 12));
        stockLabel.setStyle("-fx-text-fill: " + (product.getStock() < 5 ? "#ff6b6b" : "#666") + ";");
        
        HBox qtyBox = new HBox(5);
        qtyBox.setAlignment(Pos.CENTER_LEFT);
        Label qtyLabel = new Label("Qty:");
        qtyLabel.setFont(Font.font("Arial", 12));
        
        Spinner<Integer> qtySpinner = new Spinner<>(1, product.getStock(), 1);
        qtySpinner.setPrefWidth(70);
        qtySpinner.setEditable(true);
        
        qtyBox.getChildren().addAll(qtyLabel, qtySpinner);
        
        Button addButton = new Button("Add to Cart");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        addButton.setPrefWidth(190);
        addButton.setPrefHeight(35);
        
        if (product.getStock() == 0) {
            addButton.setDisable(true);
            addButton.setText("Out of Stock");
            addButton.setStyle("-fx-background-color: #ccc; -fx-text-fill: #666; -fx-font-size: 14px;");
        }
        
        addButton.setOnAction(e -> {
            int qty = qtySpinner.getValue();
            if (qty > 0 && qty <= product.getStock()) {
                customer.addToCart(product, qty);
                updateCartCount();
                showNotification("Added " + qty + "x " + product.getName() + " to cart");
                qtySpinner.getValueFactory().setValue(1);
            }
        });
        
        card.getChildren().addAll(nameLabel, sep, priceLabel, stockLabel, qtyBox, addButton);
        
        return card;
    }
    
    /**
     * Updates the cart item count display.
     */
    public void updateCartCount() {
        int itemCount = customer.getCart().getItems().size();
        cartItemCountLabel.setText("Cart: " + itemCount + " items");
    }
    
    /**
     * Shows a notification message.
     */
    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Refreshes the entire view.
     */
    public void refresh() {
        mainCategoryTabs.getTabs().clear();
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        for (Map.Entry<String, Map<String, List<Product>>> entry : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(entry.getKey(), entry.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        updateCartCount();
    }
}