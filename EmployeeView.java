import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;

/**
 * EmployeeView displays the employee dashboard with inventory management.
 * Allows employees to manage products, view inventory, and restock items.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class EmployeeView extends BorderPane {
    private ConvenienceStore store;
    private Employee employee;
    private MainApplication mainApp;
    
    private TableView<Product> productTable;
    
    /**
     * Constructs an EmployeeView.
     *
     * @param store the convenience store
     * @param employee the logged in employee
     * @param mainApp the main application
     */
    public EmployeeView(ConvenienceStore store, Employee employee, MainApplication mainApp) {
        this.store = store;
        this.employee = employee;
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
        
        // Center: Tabs for different functions
        TabPane tabPane = new TabPane();
        
        Tab inventoryTab = new Tab("Inventory", createInventoryView());
        Tab restockTab = new Tab("Restock", createRestockView());
        Tab addProductTab = new Tab("Add Product", createAddProductView());
        Tab salesTab = new Tab("Sales History", createSalesView());
        
        inventoryTab.setClosable(false);
        restockTab.setClosable(false);
        addProductTab.setClosable(false);
        salesTab.setClosable(false);
        
        tabPane.getTabs().addAll(inventoryTab, restockTab, addProductTab, salesTab);
        
        setCenter(tabPane);
    }
    
    /**
     * Creates the top bar.
     */
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #FF9800;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Employee Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label employeeLabel = new Label("👤 " + employee.getName() + " (ID: " + employee.getEmployeeID() + ")");
        employeeLabel.setFont(Font.font("Arial", 14));
        employeeLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> mainApp.logout());
        
        topBar.getChildren().addAll(titleLabel, spacer, employeeLabel, logoutButton);
        return topBar;
    }
    
    /**
     * Creates the inventory view.
     */
    private VBox createInventoryView() {
        VBox inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Current Inventory");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Create inventory display
        TextArea inventoryArea = new TextArea();
        inventoryArea.setEditable(false);
        inventoryArea.setFont(Font.font("Courier New", 12));
        inventoryArea.setPrefRowCount(25);
        
        // Display inventory
        StringBuilder sb = new StringBuilder();
        for (Shelf shelf : store.getInventory().getShelves()) {
            sb.append("=== ").append(shelf.getCategory().getName().toUpperCase())
              .append(" - ").append(shelf.getCategory().getType().toUpperCase()).append(" ===\n");
            
            for (Product p : shelf.getProducts()) {
                sb.append(String.format("ID: %d | %s | Price: ₱%.2f | Stock: %d",
                         p.getProductID(), p.getName(), p.getPrice(), p.getStock()));
                
                if (p.getBrand() != null) {
                    sb.append(" | Brand: ").append(p.getBrand());
                }
                if (p.getVariant() != null) {
                    sb.append(" ").append(p.getVariant());
                }
                if (p.getExpirationDate() != null) {
                    sb.append(" | Exp: ").append(p.getExpirationDate());
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        
        inventoryArea.setText(sb.toString());
        
        Button refreshButton = new Button("Refresh Inventory");
        refreshButton.setOnAction(e -> {
            // Refresh logic
            inventoryArea.setText(sb.toString());
        });
        
        inventoryBox.getChildren().addAll(titleLabel, inventoryArea, refreshButton);
        return inventoryBox;
    }
    
    /**
     * Creates the restock view.
     */
    private VBox createRestockView() {
        VBox restockBox = new VBox(15);
        restockBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Restock Items");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Product selection
        Label productLabel = new Label("Select Product:");
        ComboBox<String> productCombo = new ComboBox<>();
        
        for (Product p : store.getInventory().getProducts()) {
            productCombo.getItems().add(p.getProductID() + " - " + p.getName());
        }
        productCombo.setPrefWidth(300);
        
        // Quantity input
        Label qtyLabel = new Label("Quantity to Add:");
        Spinner<Integer> qtySpinner = new Spinner<>(1, 1000, 10);
        qtySpinner.setPrefWidth(150);
        
        Button restockButton = new Button("Restock");
        restockButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        restockButton.setPrefWidth(200);
        
        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        
        restockButton.setOnAction(e -> {
            String selected = productCombo.getValue();
            if (selected == null) {
                statusLabel.setText("Please select a product");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            int productId = Integer.parseInt(selected.split(" - ")[0]);
            Product product = null;
            for (Product p : store.getInventory().getProducts()) {
                if (p.getProductID() == productId) {
                    product = p;
                    break;
                }
            }
            
            if (product != null) {
                int qty = qtySpinner.getValue();
                employee.restockItem(store.getInventory(), product, qty);
                statusLabel.setText("Successfully restocked " + product.getName() + " by " + qty + " units");
                statusLabel.setStyle("-fx-text-fill: green;");
            }
        });
        
        // Low stock alerts
        Label lowStockLabel = new Label("Low Stock Alerts:");
        lowStockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        ListView<String> lowStockList = new ListView<>();
        ArrayList<Product> lowStock = store.getInventory().flagLowStock();
        
        if (lowStock.isEmpty()) {
            lowStockList.getItems().add("No low stock items");
        } else {
            for (Product p : lowStock) {
                lowStockList.getItems().add(p.getName() + " - Only " + p.getStock() + " units left");
            }
        }
        lowStockList.setPrefHeight(150);
        
        restockBox.getChildren().addAll(
            titleLabel,
            productLabel, productCombo,
            qtyLabel, qtySpinner,
            restockButton,
            statusLabel,
            new Separator(),
            lowStockLabel, lowStockList
        );
        
        return restockBox;
    }
    
    /**
     * Creates the add product view.
     */
    private VBox createAddProductView() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Add New Product");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        // Form fields
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Food-Snacks", "Food-Vegetables", "Food-Fruits", "Beverages-Cold Drinks");
        
        formGrid.add(new Label("Product ID:"), 0, 0);
        formGrid.add(idField, 1, 0);
        formGrid.add(new Label("Name:"), 0, 1);
        formGrid.add(nameField, 1, 1);
        formGrid.add(new Label("Price:"), 0, 2);
        formGrid.add(priceField, 1, 2);
        formGrid.add(new Label("Stock:"), 0, 3);
        formGrid.add(stockField, 1, 3);
        formGrid.add(new Label("Category:"), 0, 4);
        formGrid.add(categoryCombo, 1, 4);
        
        Label statusLabel = new Label("");
        
        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                
                String[] catParts = categoryCombo.getValue().split("-");
                Category category = new Category(catParts[0], catParts[1]);
                
                Product product = new Product(id, name, price, stock, category);
                employee.addProduct(store.getInventory(), product);
                
                statusLabel.setText("Product added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                
                // Clear fields
                idField.clear();
                nameField.clear();
                priceField.clear();
                stockField.clear();
                
            } catch (Exception ex) {
                statusLabel.setText("Error: " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        });
        
        addBox.getChildren().addAll(titleLabel, formGrid, addButton, statusLabel);
        return addBox;
    }
    
    /**
     * Creates the sales history view.
     */
    private VBox createSalesView() {
        VBox salesBox = new VBox(15);
        salesBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Sales History");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TextArea salesArea = new TextArea();
        salesArea.setEditable(false);
        salesArea.setFont(Font.font("Courier New", 12));
        
        StringBuilder sb = new StringBuilder();
        sb.append("SALES HISTORY\n");
        sb.append("=".repeat(50)).append("\n\n");
        
        // Display sales from store
        sb.append("Total Transactions: Coming soon...\n");
        
        salesArea.setText(sb.toString());
        
        salesBox.getChildren().addAll(titleLabel, salesArea);
        return salesBox;
    }
}