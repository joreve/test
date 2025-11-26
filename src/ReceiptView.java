import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

/**
 * ReceiptView displays a formatted receipt in a separate window.
 * Shows itemized purchases, discounts, totals, and payment information.
 * Receipt is automatically saved to file when created.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class ReceiptView extends Stage {
    private Receipt receipt;
    private Transaction transaction;
    private TextArea receiptTextArea;

    public ReceiptView(Receipt receipt) {
        this.receipt = receipt;
        this.transaction = getTransactionFromReceipt(receipt);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Receipt - " + transaction.getTransactionID());

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: white;");

        // Header
        Label headerLabel = new Label("CONVENIENCE STORE");
        headerLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));

        Label receiptLabel = new Label("RECEIPT");
        receiptLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));

        VBox headerBox = new VBox(5, headerLabel, receiptLabel);
        headerBox.setAlignment(Pos.CENTER);

        Separator sep1 = new Separator();

        // Receipt content
        receiptTextArea = new TextArea();
        receiptTextArea.setFont(Font.font("Courier New", 12));
        receiptTextArea.setEditable(false);
        receiptTextArea.setPrefRowCount(25);
        receiptTextArea.setPrefColumnCount(50);
        receiptTextArea.setStyle("-fx-control-inner-background: #f9f9f9;");

        // Generate receipt text
        String receiptText = generateReceiptText();
        receiptTextArea.setText(receiptText);

        Separator sep2 = new Separator();

        // Info label
        Label infoLabel = new Label("✓ Receipt automatically saved to file");
        infoLabel.setFont(Font.font("Arial", 12));
        infoLabel.setStyle("-fx-text-fill: #4CAF50;");

        // Buttons - Removed Save button
        Button printButton = new Button("Print Receipt");
        printButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");
        printButton.setPrefWidth(150);
        printButton.setOnAction(e -> handlePrint());

        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-font-size: 14px;");
        closeButton.setPrefWidth(150);
        closeButton.setOnAction(e -> close());

        HBox buttonBox = new HBox(10, printButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER);

        mainLayout.getChildren().addAll(headerBox, sep1, receiptTextArea, sep2, infoLabel, buttonBox);

        Scene scene = new Scene(mainLayout, 600, 700);
        setScene(scene);
    }

    private String generateReceiptText() {
        StringBuilder sb = new StringBuilder();

        sb.append("========================================\n");
        sb.append("          CONVENIENCE STORE\n");
        sb.append("             RECEIPT\n");
        sb.append("========================================\n\n");

        // Transaction info
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        sb.append("Date: ").append(transaction.getTimeStamp().format(formatter)).append("\n");
        sb.append("Transaction ID: ").append(transaction.getTransactionID()).append("\n");
        sb.append("Customer: ").append(transaction.getCustomer().getName()).append("\n");

        // Membership info
        if (transaction.getCustomer().hasMembershipCard()) {
            MembershipCard card = transaction.getCustomer().getMembershipCard();
            sb.append("Member Card: ").append(card.getCardNumber()).append("\n");
            sb.append("Points Balance: ").append(card.getPoints()).append("\n");
        }

        sb.append("\n========================================\n");
        sb.append("ITEMS:\n");
        sb.append("----------------------------------------\n");

        // Items
        double subtotal = 0.0;
        for (CartItem item : transaction.getPurchasedItems()) {
            Product product = item.getProduct();
            double lineTotal = item.computeLineTotal();
            subtotal += lineTotal;

            sb.append(String.format("%-20s x%-3d  ₱%8.2f\n",
                    truncate(product.getName(), 20),
                    item.getQuantity(),
                    lineTotal));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:               ₱%8.2f\n", subtotal));

        // Discounts
        double afterDiscount = transaction.applyDiscounts();
        double discount = subtotal - afterDiscount;
        if (discount > 0) {
            sb.append(String.format("Discount:              -₱%8.2f\n", discount));
            sb.append(String.format("After Discount:         ₱%8.2f\n", afterDiscount));
        }

        // VAT
        double vat = afterDiscount * 0.12;
        sb.append(String.format("VAT (12%%):              ₱%8.2f\n", vat));

        sb.append("========================================\n");
        sb.append(String.format("TOTAL:                  ₱%8.2f\n", transaction.getTotalCost()));

        if (transaction.getPayment() != null) {
            sb.append(String.format("Amount Received:        ₱%8.2f\n",
                    transaction.getPayment().getAmountReceived()));
            sb.append(String.format("Change:                 ₱%8.2f\n",
                    transaction.getPayment().computeChange()));
        }

        sb.append("========================================\n");
        sb.append("     Thank you for shopping with us!\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    private void handlePrint() {
        // In a real application, this would send to a printer
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print");
        alert.setHeaderText("Print Receipt");
        alert.setContentText("Receipt sent to printer.\n(In a real app, this would print)");
        alert.showAndWait();
    }

    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    private Transaction getTransactionFromReceipt(Receipt receipt) {
        try {
            java.lang.reflect.Field field = Receipt.class.getDeclaredField("transaction");
            field.setAccessible(true);
            return (Transaction) field.get(receipt);
        } catch (Exception e) {
            throw new RuntimeException("Cannot access transaction from receipt", e);
        }
    }
}