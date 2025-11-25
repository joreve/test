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
 * EmployeeView displays the employee dashboard with inventory management.
 * Updated with improved UI organization and product category tabs.
 *
 * @author Joreve P. De Jesus
 */
public class EmployeeView extends BorderPane {
    private ConvenienceStore store;
    private Employee employee;
    private MainApplication mainApp;
    private DataManager dataManager;
    
    private TabPane mainCategoryTabs;
    private VBox lowStockBox;
    private VBox expiryAlertBox;
    private int currentMainTabIndex = 0;
    private Map<String, Integer> subTabIndices = new HashMap<>();
    
    public EmployeeView(ConvenienceStore store, Employee employee, MainApplication mainApp, DataManager dataManager) {
        this.store = store;
        this.employee = employee;
        this.mainApp = mainApp;
        this.dataManager = dataManager;
        initializeUI();
    }
    
    private void initializeUI() {
        HBox topBar = createTopBar();
        setTop(topBar);
        
        TabPane mainTabs = new TabPane();
        mainTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab inventoryTab = new Tab("Inventory Management", createInventoryManagementView());
        Tab addProductTab = new Tab("Add Product", createAddProductView());
        Tab salesTab = new Tab("Sales History", createSalesView());
        
        mainTabs.getTabs().addAll(inventoryTab, addProductTab, salesTab);
        setCenter(mainTabs);
    }
    
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
    
    private VBox createInventoryManagementView() {
        VBox inventoryBox = new VBox(15);
        inventoryBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Inventory Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Alert boxes
        lowStockBox = createLowStockAlert();
        expiryAlertBox = createExpiryAlert();
        
        // Main category tabs (Food, Beverages)
        mainCategoryTabs = new TabPane();
        mainCategoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        for (Map.Entry<String, Map<String, List<Product>>> mainCat : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(mainCat.getKey(), mainCat.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        // Track tab changes
        mainCategoryTabs.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentMainTabIndex = newVal.intValue();
        });
        
        inventoryBox.getChildren().addAll(titleLabel, lowStockBox, expiryAlertBox, new Separator(), mainCategoryTabs);
        return inventoryBox;
    }
    
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
    
    private VBox createExpiryAlert() {
        VBox alertBox = new VBox(10);
        alertBox.setPadding(new Insets(15));
        alertBox.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f44336; -fx-border-width: 2; -fx-border-radius: 5;");
        
        Label alertTitle = new Label("📅 Expiration Alerts (≤15 days)");
        alertTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        alertTitle.setStyle("-fx-text-fill: #721c24;");
        
        FlowPane expiryFlow = new FlowPane();
        expiryFlow.setHgap(10);
        expiryFlow.setVgap(5);
        
        ArrayList<Product> expiringProducts = store.getInventory().flagExpiringProducts(15);
        
        if (expiringProducts.isEmpty()) {
            Label noAlerts = new Label("No products expiring soon");
            noAlerts.setStyle("-fx-text-fill: #721c24;");
            expiryFlow.getChildren().add(noAlerts);
        } else {
            for (Product p : expiringProducts) {
                long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), p.getExpirationDate());
                Label itemLabel = new Label(p.getName() + ": " + daysUntilExpiry + " days");
                itemLabel.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                                 "-fx-padding: 5 10; -fx-background-radius: 3;");
                expiryFlow.getChildren().add(itemLabel);
            }
        }
        
        alertBox.getChildren().addAll(alertTitle, expiryFlow);
        return alertBox;
    }
    
    private Map<String, Map<String, List<Product>>> organizeProductsByCategory() {
        Map<String, Map<String, List<Product>>> organized = new LinkedHashMap<>();
        
        for (Shelf shelf : store.getInventory().getShelves()) {
            String mainCat = shelf.getCategory().getName();
            String subCat = shelf.getCategory().getType();
            
            organized.putIfAbsent(mainCat, new LinkedHashMap<>());
            organized.get(mainCat).putIfAbsent(subCat, new ArrayList<>());
            organized.get(mainCat).get(subCat).addAll(shelf.getProducts());
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
        
        // Track sub-tab changes
        subTabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            subTabIndices.put(mainCategory, newVal.intValue());
        });
        
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
        
        for (Product product : products) {
            VBox productCard = createProductManagementCard(product);
            productFlow.getChildren().add(productCard);
        }
        
        scrollPane.setContent(productFlow);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private VBox createProductManagementCard(Product product) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 8; " +
                     "-fx-background-color: white; -fx-background-radius: 8;");
        card.setPrefWidth(240);
        
        Label nameLabel = new Label(product.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setWrapText(true);
        
        VBox detailsBox = new VBox(5);
        detailsBox.getChildren().add(new Label("ID: " + product.getProductID()));
        detailsBox.getChildren().add(new Label("Price: ₱" + String.format("%.2f", product.getPrice())));
        
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
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
            Label expLabel = new Label("Exp: " + product.getExpirationDate());
            if (daysUntilExpiry <= 15) {
                expLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
            detailsBox.getChildren().add(expLabel);
        }
        
        Separator sep = new Separator();
        
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
            dataManager.saveProducts(store.getInventory().getProducts());
            refreshInventory();
            showAlert("Success", "Product restocked successfully!", Alert.AlertType.INFORMATION);
        });
    }
    
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
        TextField brandField = new TextField(product.getBrand() != null ? product.getBrand() : "");
        TextField variantField = new TextField(product.getVariant() != null ? product.getVariant() : "");
        DatePicker expDatePicker = new DatePicker(product.getExpirationDate());
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Food-Snacks", "Food-Vegetables", "Food-Fruits", 
                                       "Beverages-Cold Drinks", "Beverages-Juice");
        String currentCategory = product.getCategory().getName() + "-" + product.getCategory().getType();
        categoryCombo.setValue(currentCategory);
        
        int row = 0;
        grid.add(new Label("Product ID: " + product.getProductID()), 0, row, 2, 1);
        row++;
        grid.add(new Label("Current Stock: " + product.getStock()), 0, row, 2, 1);
        row++;
        grid.add(new Label("Name:"), 0, row);
        grid.add(nameField, 1, row);
        row++;
        grid.add(new Label("Price:"), 0, row);
        grid.add(priceField, 1, row);
        row++;
        grid.add(new Label("Category:"), 0, row);
        grid.add(categoryCombo, 1, row);
        row++;
        grid.add(new Label("Brand:"), 0, row);
        grid.add(brandField, 1, row);
        row++;
        grid.add(new Label("Variant:"), 0, row);
        grid.add(variantField, 1, row);
        row++;
        grid.add(new Label("Expiration:"), 0, row);
        grid.add(expDatePicker, 1, row);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    
                    String[] catParts = categoryCombo.getValue().split("-");
                    Category category = new Category(catParts[0], catParts[1]);
                    
                    String brand = brandField.getText().isEmpty() ? null : brandField.getText();
                    String variant = variantField.getText().isEmpty() ? null : variantField.getText();
                    LocalDate expDate = expDatePicker.getValue();
                    
                    return new Product(product.getProductID(), name, price, product.getStock(),
                                     category, brand, variant, expDate);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid price value", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
        
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updatedProduct -> {
            employee.updateProductInfo(store.getInventory(), updatedProduct);
            dataManager.saveProducts(store.getInventory().getProducts());
            refreshInventory();
            showAlert("Success", "Product updated successfully!", Alert.AlertType.INFORMATION);
        });
    }
    
    private void showRemoveDialog(Product product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Product");
        confirm.setHeaderText("Remove: " + product.getName());
        confirm.setContentText("Are you sure you want to remove this product from inventory?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                store.getInventory().removeProduct(product.getProductID());
                dataManager.saveProducts(store.getInventory().getProducts());
                refreshInventory();
                showAlert("Success", "Product removed successfully!", Alert.AlertType.INFORMATION);
            }
        });
    }
    
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
                
                if (store.getInventory().productExists(id)) {
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
                dataManager.saveProducts(store.getInventory().getProducts());
                
                for (Shelf shelf : store.getInventory().getShelves()) {
                    if (shelf.getCategory().getName().equals(category.getName()) &&
                        shelf.getCategory().getType().equals(category.getType())) {
                        shelf.addProduct(product);
                        break;
                    }
                }
                
                statusLabel.setText("Product added successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
                
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
        
        List<String> transactions = dataManager.loadTransactions();
        
        if (transactions.isEmpty()) {
            sb.append("No transactions yet.\n");
        } else {
            for (String transaction : transactions) {
                String[] parts = transaction.split("\\|\\|\\|");
                if (parts.length >= 4) {
                    sb.append(String.format("Transaction ID: %s\n", parts[0]));
                    sb.append(String.format("Customer: %s\n", parts[1]));
                    sb.append(String.format("Total: ₱%.2f\n", Double.parseDouble(parts[2])));
                    sb.append(String.format("Date: %s\n", parts[3]));
                    sb.append("-".repeat(80)).append("\n");
                }
            }
        }
        
        salesArea.setText(sb.toString());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            VBox refreshed = createSalesView();
            Tab currentTab = ((TabPane)((VBox)salesBox.getParent()).getParent()).getSelectionModel().getSelectedItem();
            currentTab.setContent(refreshed);
        });
        
        salesBox.getChildren().addAll(titleLabel, salesArea, refreshButton);
        return salesBox;
    }
    
    private void refreshInventory() {
        // Save current tab positions
        String currentMainCategory = null;
        if (currentMainTabIndex >= 0 && currentMainTabIndex < mainCategoryTabs.getTabs().size()) {
            currentMainCategory = mainCategoryTabs.getTabs().get(currentMainTabIndex).getText();
        }
        
        // Clear and rebuild
        mainCategoryTabs.getTabs().clear();
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        for (Map.Entry<String, Map<String, List<Product>>> mainCat : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(mainCat.getKey(), mainCat.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        // Restore main tab position
        if (currentMainCategory != null) {
            for (int i = 0; i < mainCategoryTabs.getTabs().size(); i++) {
                if (mainCategoryTabs.getTabs().get(i).getText().equals(currentMainCategory)) {
                    mainCategoryTabs.getSelectionModel().select(i);
                    
                    // Restore sub-tab position
                    Integer subTabIndex = subTabIndices.get(currentMainCategory);
                    if (subTabIndex != null) {
                        TabPane subTabPane = (TabPane) mainCategoryTabs.getTabs().get(i).getContent();
                        if (subTabIndex < subTabPane.getTabs().size()) {
                            subTabPane.getSelectionModel().select(subTabIndex);
                        }
                    }
                    break;
                }
            }
        }
        
        // Refresh alerts
        VBox parent = (VBox) lowStockBox.getParent();
        int lowStockIndex = parent.getChildren().indexOf(lowStockBox);
        int expiryIndex = parent.getChildren().indexOf(expiryAlertBox);
        
        parent.getChildren().set(lowStockIndex, createLowStockAlert());
        parent.getChildren().set(expiryIndex, createExpiryAlert());
        
        lowStockBox = (VBox) parent.getChildren().get(lowStockIndex);
        expiryAlertBox = (VBox) parent.getChildren().get(expiryIndex);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}