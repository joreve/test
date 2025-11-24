import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;

/**
 * EmployeeView displays the employee dashboard with inventory management.
 * Shows products in organized tabs with restock and edit functionality.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class EmployeeView extends BorderPane {
    private ConvenienceStore store;
    private Employee employee;
    private MainApplication mainApp;
    private ProductDataManager productDataManager;
    
    private TabPane categoryTabs;
    
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
        this.productDataManager = new ProductDataManager();
        initializeUI();
    }
    
    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        // Top bar
        HBox topBar = createTopBar();
        setTop(topBar);
        
        // Center: Main content with tabs
        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab inventoryTab = new Tab("Inventory Management", createInventoryManagementView());
        Tab addProductTab = new Tab("Add Product", createAddProductView());
        Tab salesTab = new Tab("Sales History", createSalesView());
        
        mainTabs.getTabs().addAll(inventoryTab, addProductTab, salesTab);
        
        setCenter(mainTabs);
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
     * Creates the inventory management view with category tabs.
     */
    private VBox createInventoryManagementView() {
        VBox inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Inventory Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Low stock alert section
        VBox lowStockBox = createLowStockAlert();
        
        // Category tabs for products
        categoryTabs = new TabPane();
        categoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Map<String, List<Product>> productsByCategory = groupProductsByCategory();
        
        for (Map.Entry<String, List<Product>> entry : productsByCategory.entrySet()) {
            Tab tab = createProductManagementTab(entry.getKey(), entry.getValue());
            categoryTabs.getTabs().add(tab);
        }
        
        inventoryBox.getChildren().addAll(titleLabel, lowStockBox, new Separator(), categoryTabs);
        return inventoryBox;
    }
    
    /**
     * Creates low stock alert section.
     */
    private VBox createLowStockAlert() {
        VBox alertBox = new VBox(10);
        alertBox.setPadding(new Insets(15));
        alertBox.setStyle("-fx-background-color: #fff3cd; -fx-border-color: #ffc107; -fx-border-width: 2; -fx-border-radius: 5;");
        
        Label alertTitle = new Label("⚠️ Low Stock Alerts");
        alertTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        alertTitle.setStyle("-fx-text-fill: #856404;");
        
        FlowPane lowStockFlow = new FlowPane();
        lowStockFlow.setHgap(10);
        lowStockFlow.setVgap(5);
        
        ArrayList<Product> lowStock = store.getInventory().flagLowStock();
        
        if (lowStock.isEmpty()) {
            Label noAlerts = new Label("No low stock items");
            noAlerts.setStyle("-fx-text-fill: #856404;");
            lowStockFlow.getChildren().add(noAlerts);
        } else {
            for (Product p : lowStock) {
                Label itemLabel = new Label(p.getName() + ": " + p.getStock() + " units");
                itemLabel.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; " +
                                 "-fx-padding: 5 10; -fx-background-radius: 3;");
                lowStockFlow.getChildren().add(itemLabel);
            }
        }
        
        alertBox.getChildren().addAll(alertTitle, lowStockFlow);
        return alertBox;
    }
    
    /**
     * Groups products by category.
     */
    private Map<String, List<Product>> groupProductsByCategory() {
        Map<String, List<Product>> grouped = new LinkedHashMap<>();
        
        for (Shelf shelf : store.getInventory().getShelves()) {
            String categoryKey = shelf.getCategory().getName() + " - " + shelf.getCategory().getType();
            
            if (!grouped.containsKey(categoryKey)) {
                grouped.put(categoryKey, new ArrayList<>());
            }
            
            grouped.get(categoryKey).addAll(shelf.getProducts());
        }
        
        return grouped;
    }
    
    /**
     * Creates a product management tab for a category.
     */
    private Tab createProductManagementTab(String categoryName, List<Product> products) {
        Tab tab = new Tab(categoryName);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: white;");
        
        FlowPane productFlow = new FlowPane();
        productFlow.setHgap(20);
        productFlow.setVgap(20);
        productFlow.setPadding(new Insets(20));
        
        for (Product product : products) {
            VBox productCard = createProductManagementCard(product);
            productFlow.getChildren().add(productCard);
        }
        
        scrollPane.setContent(productFlow);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    /**
     * Creates a product management card with restock and edit buttons.
     */
    private VBox createProductManagementCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 8; " +
                     "-fx-background-color: white; -fx-background-radius: 8;");
        card.setPrefWidth(240);
        
        // Product name
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        
        // Details
        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().add(new Label("ID: " + product.getProductID()));
        detailsBox.getChildren().add(new Label("Price: ₱" + String.format("%.2f", product.getPrice())));
        
        // Stock with low stock flag
        HBox stockBox = new HBox(5);
        stockBox.setAlignment(Pos.CENTER_LEFT);
        Label stockLabel = new Label("Stock: " + product.getStock());
        stockBox.getChildren().add(stockLabel);
        
        if (product.getStock() < 5) {
            Label lowStockFlag = new Label("LOW STOCK");
            lowStockFlag.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 10px;");
            stockBox.getChildren().add(lowStockFlag);
        }
        
        detailsBox.getChildren().add(stockBox);
        
        if (product.getBrand() != null) {
            detailsBox.getChildren().add(new Label("Brand: " + product.getBrand()));
        }
        if (product.getVariant() != null) {
            detailsBox.getChildren().add(new Label("Variant: " + product.getVariant()));
        }
        if (product.getExpirationDate() != null) {
            detailsBox.getChildren().add(new Label("Exp: " + product.getExpirationDate()));
        }
        
        Separator sep = new Separator();
        
        // Buttons
        Button restockButton = new Button("Restock");
        restockButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        restockButton.setPrefWidth(100);
        restockButton.setOnAction(e -> showRestockDialog(product));
        
        Button editButton = new Button("Edit");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editButton.setPrefWidth(100);
        editButton.setOnAction(e -> showEditDialog(product));
        
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setPrefWidth(100);
        removeButton.setOnAction(e -> showRemoveDialog(product));
        
        HBox buttonBox = new HBox(5, restockButton, editButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(nameLabel, detailsBox, sep, buttonBox, removeButton);
        
        return card;
    }
    
    /**
     * Shows restock dialog.
     */
    private void showRestockDialog(Product product) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Restock Product");
        dialog.setHeaderText("Restock: " + product.getName());
        
        ButtonType restockButtonType = new ButtonType("Restock", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(restockButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        Label currentStockLabel = new Label("Current Stock: " + product.getStock());
        Spinner<Integer> qtySpinner = new Spinner<>(1, 1000, 10);
        qtySpinner.setEditable(true);
        qtySpinner.setPrefWidth(100);
        
        grid.add(currentStockLabel, 0, 0);
        grid.add(new Label("Add Quantity:"), 0, 1);
        grid.add(qtySpinner, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == restockButtonType) {
                return qtySpinner.getValue();
            }
            return null;
        });
        
        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            employee.restockItem(store.getInventory(), product, quantity);
            productDataManager.updateProduct(product);
            refreshInventory();
            showAlert("Success", "Product restocked successfully!", Alert.AlertType.INFORMATION);
        });
    }
    
    /**
     * Shows edit product dialog.
     */
    private void showEditDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit: " + product.getName());
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField(product.getName());
        TextField priceField = new TextField(String.valueOf(product.getPrice()));
        TextField stockField = new TextField(String.valueOf(product.getStock()));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(stockField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    
                    return new Product(product.getProductID(), name, price, stock,
                                     product.getCategory(), product.getBrand(),
                                     product.getVariant(), product.getExpirationDate());
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid price or stock value", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updatedProduct -> {
            employee.updateProductInfo(store.getInventory(), updatedProduct);
            productDataManager.updateProduct(updatedProduct);
            refreshInventory();
            showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
        });
    }
    
    /**
     * Shows remove product confirmation dialog.
     */
    private void showRemoveDialog(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Product");
        confirm.setHeaderText("Remove: " + product.getName());
        confirm.setContentText("Are you sure you want to remove this product from inventory?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                store.getInventory().removeProduct(product.getProductID());
                productDataManager.removeProduct(product.getProductID());
                refreshInventory();
                showAlert("Success", "Product removed successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    /**
     * Creates the add product view.
     */
    private VBox createAddProductView() {
        VBox addBox = new VBox(15);
        addBox.setPadding(new Insets(20));
        addBox.setAlignment(Pos.TOP_CENTER);
        
        Label titleLabel = new Label("Add New Product");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setMaxWidth(500);
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        TextField brandField = new TextField();
        TextField variantField = new TextField();
        DatePicker expDatePicker = new DatePicker();
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Food-Snacks", "Food-Vegetables", "Food-Fruits", 
                                       "Beverages-Cold Drinks", "Beverages-Juice");
        categoryCombo.setPrefWidth(200);
        
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
        formGrid.add(new Label("Brand (optional):"), 0, 5);
        formGrid.add(brandField, 1, 5);
        formGrid.add(new Label("Variant (optional):"), 0, 6);
        formGrid.add(variantField, 1, 6);
        formGrid.add(new Label("Expiration (optional):"), 0, 7);
        formGrid.add(expDatePicker, 1, 7);
        
        Label statusLabel = new Label("");
        statusLabel.setFont(Font.font("Arial", 12));
        
        Button addButton = new Button("Add Product");
        addButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px;");
        addButton.setPrefWidth(200);
        addButton.setPrefHeight(40);
        
        addButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                
                if (productDataManager.productExists(id)) {
                    statusLabel.setText("Product ID already exists!");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    return;
                }
                
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                
                String[] catParts = categoryCombo.getValue().split("-");
                Category category = new Category(catParts[0], catParts[1]);
                
                String brand = brandField.getText().isEmpty() ? null : brandField.getText();
                String variant = variantField.getText().isEmpty() ? null : variantField.getText();
                LocalDate expDate = expDatePicker.getValue();
                
                Product product = new Product(id, name, price, stock, category, brand, variant, expDate);
                employee.addProduct(store.getInventory(), product);
                productDataManager.addProduct(product);
                
                // Add to appropriate shelf
                for (Shelf shelf : store.getInventory().getShelves()) {
                    if (shelf.getCategory().getName().equals(category.getName()) &&
                        shelf.getCategory().getType().equals(category.getType())) {
                        shelf.addProduct(product);
                        break;
                    }
                }
                
                statusLabel.setText("Product added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                
                // Clear fields
                idField.clear();
                nameField.clear();
                priceField.clear();
                stockField.clear();
                brandField.clear();
                variantField.clear();
                expDatePicker.setValue(null);
                
                refreshInventory();
                
            } catch (NumberFormatException ex) {
                statusLabel.setText("Error: Invalid number format");
                statusLabel.setStyle("-fx-text-fill: red;");
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
        salesArea.setPrefRowCount(30);
        
        StringBuilder sb = new StringBuilder();
        sb.append("SALES HISTORY\n");
        sb.append("=".repeat(80)).append("\n\n");
        
        // Get sales from store
        ArrayList<Transaction> salesHistory = store.getSalesHistory();
        
        if (salesHistory.isEmpty()) {
            sb.append("No transactions yet.\n");
        } else {
            for (Transaction t : salesHistory) {
                sb.append(String.format("Transaction ID: %s\n", t.getTransactionID()));
                sb.append(String.format("Customer: %s\n", t.getCustomer().getName()));
                sb.append(String.format("Date: %s\n", t.getTimeStamp()));
                sb.append(String.format("Total: ₱%.2f\n", t.getTotalCost()));
                sb.append(String.format("Items: %d\n", t.getPurchasedItems().size()));
                sb.append("-".repeat(80)).append("\n");
            }
        }
        
        salesArea.setText(sb.toString());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            // Refresh sales history
            VBox refreshed = createSalesView();
            ((VBox)salesBox.getParent()).getChildren().set(
                ((VBox)salesBox.getParent()).getChildren().indexOf(salesBox), refreshed);
        });
        
        salesBox.getChildren().addAll(titleLabel, salesArea, refreshButton);
        return salesBox;
    }
    
    /**
     * Refreshes the inventory display.
     */
    private void refreshInventory() {
        // Clear and rebuild category tabs
        categoryTabs.getTabs().clear();
        
        Map<String, List<Product>> productsByCategory = groupProductsByCategory();
        for (Map.Entry<String, List<Product>> entry : productsByCategory.entrySet()) {
            Tab tab = createProductManagementTab(entry.getKey(), entry.getValue());
            categoryTabs.getTabs().add(tab);
        }
    }
    
    /**
     * Shows an alert dialog.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}