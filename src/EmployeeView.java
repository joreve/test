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
 * EmployeeView displays the employee dashboard with inventory management tabs.
 * NO business logic - only UI display and user input capture.
 *
 * @author Joreve P. De Jesus
 */
public class EmployeeView extends BorderPane {
    private ConvenienceStore store;
    private Employee employee;
    private EmployeeController controller;
    
    private TabPane mainCategoryTabs;
    private TitledPane lowStockPane;
    private TitledPane expiryAlertPane;
    private TextArea salesArea; // Store reference for refresh
    private int currentMainTabIndex = 0;
    private Map<String, Integer> subTabIndices = new HashMap<>();
    
    public EmployeeView(ConvenienceStore store, Employee employee) {
        this.store = store;
        this.employee = employee;
        initializeUI();
    }
    
    /**
     * Injects the controller after view creation.
     */
    public void setController(EmployeeController controller) {
        this.controller = controller;
        // Now that controller is set, load the sales data
        if (salesArea != null) {
            refreshSalesDisplay(salesArea);
        }
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
        logoutButton.setOnAction(e -> {
            if (controller != null) {
                controller.handleLogout();
            }
        });
        
        topBar.getChildren().addAll(titleLabel, spacer, employeeLabel, logoutButton);
        return topBar;
    }
    
    private VBox createInventoryManagementView() {
        VBox inventoryBox = new VBox(10);
        inventoryBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Inventory Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Collapsible alert boxes
        Accordion alertAccordion = new Accordion();
        lowStockPane = createLowStockAlert();
        expiryAlertPane = createExpiryAlert();
        
        alertAccordion.getPanes().addAll(lowStockPane, expiryAlertPane);
        alertAccordion.setMaxHeight(200);
        
        // Main category tabs
        mainCategoryTabs = new TabPane();
        mainCategoryTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(mainCategoryTabs, Priority.ALWAYS);
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        for (Map.Entry<String, Map<String, List<Product>>> mainCat : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(mainCat.getKey(), mainCat.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        mainCategoryTabs.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            currentMainTabIndex = newVal.intValue();
        });
        
        inventoryBox.getChildren().addAll(titleLabel, alertAccordion, mainCategoryTabs);
        return inventoryBox;
    }
    
    private TitledPane createLowStockAlert() {
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        
        FlowPane lowStockFlow = new FlowPane();
        lowStockFlow.setHgap(10);
        lowStockFlow.setVgap(5);
        
        ArrayList<Product> lowStock = store.getInventory().flagLowStock();
        
        if (lowStock.isEmpty()) {
            Label noAlerts = new Label("No low stock items");
            noAlerts.setStyle("-fx-text-fill: #666;");
            lowStockFlow.getChildren().add(noAlerts);
        } else {
            for (Product p : lowStock) {
                Label itemLabel = new Label(p.getName() + ": " + p.getStock() + " units");
                itemLabel.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; " +
                                 "-fx-padding: 5 10; -fx-background-radius: 3;");
                lowStockFlow.getChildren().add(itemLabel);
            }
        }
        
        contentBox.getChildren().add(lowStockFlow);
        
        TitledPane pane = new TitledPane("⚠️ Low Stock Alerts (" + lowStock.size() + ")", contentBox);
        pane.setStyle("-fx-background-color: #fff3cd;");
        return pane;
    }
    
    private TitledPane createExpiryAlert() {
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        
        FlowPane expiryFlow = new FlowPane();
        expiryFlow.setHgap(10);
        expiryFlow.setVgap(5);
        
        ArrayList<Product> expiringProducts = store.getInventory().flagExpiringProducts(15);
        
        if (expiringProducts.isEmpty()) {
            Label noAlerts = new Label("No products expiring soon");
            noAlerts.setStyle("-fx-text-fill: #666;");
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
        
        contentBox.getChildren().add(expiryFlow);
        
        TitledPane pane = new TitledPane("📅 Expiration Alerts (" + expiringProducts.size() + ")", contentBox);
        pane.setStyle("-fx-background-color: #f8d7da;");
        return pane;
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
        removeButton.setOnAction(e -> {
            if (controller != null) {
                controller.handleRemoveProduct(product);
            }
        });
        
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
            if (controller != null) {
                controller.handleRestock(product, quantity);
            }
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
        List<String> allCategories = getAllCategories();
        categoryCombo.getItems().addAll(allCategories);
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Invalid price value");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        Optional<Product> result = dialog.showAndWait();
        result.ifPresent(updatedProduct -> {
            if (controller != null) {
                controller.handleEditProduct(updatedProduct);
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
        List<String> allCategories = getAllCategories();
        categoryCombo.getItems().addAll(allCategories);
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
            if (controller == null) return;
            
            try {
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                
                String[] catParts = categoryCombo.getValue().split("-");
                String brand = brandField.getText().isEmpty() ? null : brandField.getText();
                String variant = variantField.getText().isEmpty() ? null : variantField.getText();
                LocalDate expDate = expDatePicker.getValue();
                
                controller.handleAddProduct(id, name, price, stock, catParts[0], catParts[1], 
                                          brand, variant, expDate);
                
                // Clear fields after successful add
                idField.clear();
                nameField.clear();
                priceField.clear();
                stockField.clear();
                brandField.clear();
                variantField.clear();
                expDatePicker.setValue(null);
                statusLabel.setText("");
                
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
        
        salesArea = new TextArea();
        salesArea.setEditable(false);
        salesArea.setFont(Font.font("Courier New", 12));
        salesArea.setPrefRowCount(30);
        
        // Don't load data yet - wait for controller to be injected
        salesArea.setText("Loading sales data...");
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            if (controller != null) {
                refreshSalesDisplay(salesArea);
            }
        });
        
        salesBox.getChildren().addAll(titleLabel, salesArea, refreshButton);
        return salesBox;
    }
    
    private void refreshSalesDisplay(TextArea salesArea) {
        if (controller == null) {
            salesArea.setText("Controller not initialized");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("SALES HISTORY\n");
        sb.append("=".repeat(80)).append("\n\n");
        
        List<String> transactions = controller.getDataManager().loadTransactions();
        
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
    }
    
    private List<String> getAllCategories() {
        Set<String> categories = new LinkedHashSet<>();
        
        for (Shelf shelf : store.getInventory().getShelves()) {
            String category = shelf.getCategory().getName() + "-" + shelf.getCategory().getType();
            categories.add(category);
        }
        
        return new ArrayList<>(categories);
    }
    
    /**
     * Refreshes the entire inventory display.
     * Called by controller after inventory changes.
     */
    public void refreshInventory() {
        String currentMainCategory = null;
        if (currentMainTabIndex >= 0 && currentMainTabIndex < mainCategoryTabs.getTabs().size()) {
            currentMainCategory = mainCategoryTabs.getTabs().get(currentMainTabIndex).getText();
        }
        
        mainCategoryTabs.getTabs().clear();
        
        Map<String, Map<String, List<Product>>> organizedProducts = organizeProductsByCategory();
        
        for (Map.Entry<String, Map<String, List<Product>>> mainCat : organizedProducts.entrySet()) {
            Tab mainTab = createMainCategoryTab(mainCat.getKey(), mainCat.getValue());
            mainCategoryTabs.getTabs().add(mainTab);
        }
        
        if (currentMainCategory != null) {
            for (int i = 0; i < mainCategoryTabs.getTabs().size(); i++) {
                if (mainCategoryTabs.getTabs().get(i).getText().equals(currentMainCategory)) {
                    mainCategoryTabs.getSelectionModel().select(i);
                    
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
        
        // Refresh alerts with updated counts
        lowStockPane.setText("⚠️ Low Stock Alerts (" + store.getInventory().flagLowStock().size() + ")");
        lowStockPane.setContent(createLowStockAlert().getContent());
        
        expiryAlertPane.setText("📅 Expiration Alerts (" + store.getInventory().flagExpiringProducts(15).size() + ")");
        expiryAlertPane.setContent(createExpiryAlert().getContent());
    }
    
    /**
     * Shows success message after adding product.
     * Called by controller.
     */
    public void showAddProductSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product added successfully!");
        alert.showAndWait();
    }
}