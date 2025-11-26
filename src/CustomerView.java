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
 * CustomerView displays the shopping interface with products organized by categories.
 * Shows products in nested tabs (Food/Beverages > Subcategories).
 * NO business logic - only UI display and user input capture.
 *
 * @author Dana Ysabelle A. Pelagio and Joreve P. De Jesus
 */
public class CustomerView extends BorderPane {
    private ConvenienceStore store;
    private Customer customer;
    private ShoppingController controller;
    
    private TabPane mainCategoryTabs;
    private Button viewCartButton;
    private Label cartItemCountLabel;
    
    public CustomerView(ConvenienceStore store, Customer customer) {
        this.store = store;
        this.customer = customer;
        initializeUI();
    }
    
    /**
     * Injects the controller after view creation.
     */
    public void setController(ShoppingController controller) {
        this.controller = controller;
        // Update cart count immediately when view is shown
        updateCartCount();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        HBox topBar = createTopBar();
        setTop(topBar);
        
        mainCategoryTabs = new TabPane();
        mainCategoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        for (Map.Entry<String, Map<String, List<Product>>> entry : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(entry.getKey(), entry.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        setCenter(mainCategoryTabs);
        
        HBox bottomBar = createBottomBar();
        setBottom(bottomBar);
    }
    
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
        logoutButton.setOnAction(e -> {
            if (controller != null) {
                controller.handleLogout();
            }
        });
        
        topBar.getChildren().addAll(storeLabel, spacer, customerBox, logoutButton);
        return topBar;
    }
    
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
        viewCartButton.setOnAction(e -> {
            if (controller != null) {
                controller.handleViewCart();
            }
        });
        
        bottomBar.getChildren().addAll(cartItemCountLabel, viewCartButton);
        return bottomBar;
    }
    
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
    
    private Tab createMainCategoryTab(String mainCategory, Map<String, List<Product>> subCategories) {
        Tab mainTab = new Tab(mainCategory);
        
        TabPane subTabPane = new TabPane();
        subTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        for (Map.Entry<String, List<Product>> subCat : subCategories.entrySet()) {
            Tab subTab = createSubCategoryTab(subCat.getKey(), subCat.getValue());
            subTabPane.getTabs().add(subTab);
        }
        
        mainTab.setContent(subTabPane);
        return mainTab;
    }
    
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
            if (controller != null) {
                int qty = qtySpinner.getValue();
                controller.handleAddToCart(product, qty);
                qtySpinner.getValueFactory().setValue(1);
            }
        });
        
        card.getChildren().addAll(nameLabel, sep, priceLabel, stockLabel, qtyBox, addButton);
        
        return card;
    }
    
    /**
     * Updates the cart item count display.
     * Called by controller after adding items.
     */
    public void updateCartCount() {
        int itemCount = customer.getCart().getItems().size();
        cartItemCountLabel.setText("Cart: " + itemCount + " items");
    }
    
    /**
     * Shows a notification message.
     * Called by controller after successful actions.
     */
    public void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Refreshes the entire view with latest data.
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