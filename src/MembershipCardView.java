import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * MembershipCardView displays and manages membership card information.
 * Shows card details, points balance, and redemption options.
 *
 * @author Dana Ysabelle A. Pelagio
 */
public class MembershipCardView extends VBox {
    private MembershipCard membershipCard;

    private Label cardNumberLabel;
    private Label pointsLabel;
    private Label discountLabel;
    private ProgressBar pointsProgressBar;

    private TextField redeemPointsField;
    private Button redeemButton;
    private Label redeemResultLabel;

    /**
     * Constructs a MembershipCardView for the specified card.
     *
     * @param membershipCard the membership card to display
     */
    public MembershipCardView(MembershipCard membershipCard) {
        this.membershipCard = membershipCard;
        initializeUI();
    }

    /**
     * Initializes the user interface.
     */
    private void initializeUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%); " +
                "-fx-background-radius: 15;");
        setPrefWidth(350);
        setPrefHeight(250);

        // Card Title
        Label titleLabel = new Label("MEMBERSHIP CARD");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Card Number
        Label cardLabel = new Label("Card Number:");
        cardLabel.setFont(Font.font("Arial", 12));
        cardLabel.setStyle("-fx-text-fill: #f0f0f0;");

        cardNumberLabel = new Label(membershipCard.getCardNumber());
        cardNumberLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        cardNumberLabel.setStyle("-fx-text-fill: white;");

        VBox cardBox = new VBox(5, cardLabel, cardNumberLabel);

        // Points Display
        Label pointsTitleLabel = new Label("Points Balance:");
        pointsTitleLabel.setFont(Font.font("Arial", 12));
        pointsTitleLabel.setStyle("-fx-text-fill: #f0f0f0;");

        pointsLabel = new Label(membershipCard.getPoints() + " pts");
        pointsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        pointsLabel.setStyle("-fx-text-fill: #FFD700;");

        discountLabel = new Label("= ₱" + membershipCard.getDiscount() + " discount");
        discountLabel.setFont(Font.font("Arial", 14));
        discountLabel.setStyle("-fx-text-fill: #90EE90;");

        VBox pointsBox = new VBox(5, pointsTitleLabel, pointsLabel, discountLabel);
        pointsBox.setAlignment(Pos.CENTER_LEFT);

        // Progress bar to next reward (optional visual)
        pointsProgressBar = new ProgressBar();
        pointsProgressBar.setPrefWidth(300);
        pointsProgressBar.setProgress((membershipCard.getPoints() % 100) / 100.0);
        pointsProgressBar.setStyle("-fx-accent: #FFD700;");

        Label progressLabel = new Label("Progress to next 100 points");
        progressLabel.setFont(Font.font("Arial", 10));
        progressLabel.setStyle("-fx-text-fill: #f0f0f0;");

        VBox progressBox = new VBox(5, progressLabel, pointsProgressBar);

        // Redeem Section
        Label redeemLabel = new Label("Redeem Points:");
        redeemLabel.setFont(Font.font("Arial", 12));
        redeemLabel.setStyle("-fx-text-fill: #f0f0f0;");

        redeemPointsField = new TextField();
        redeemPointsField.setPromptText("Enter points to redeem");
        redeemPointsField.setPrefWidth(150);

        redeemButton = new Button("Redeem");
        redeemButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        redeemButton.setOnAction(e -> handleRedeem());

        HBox redeemBox = new HBox(10, redeemPointsField, redeemButton);
        redeemBox.setAlignment(Pos.CENTER_LEFT);

        redeemResultLabel = new Label("");
        redeemResultLabel.setFont(Font.font("Arial", 11));
        redeemResultLabel.setStyle("-fx-text-fill: #FFD700;");

        getChildren().addAll(titleLabel, cardBox, pointsBox, progressBox,
                redeemLabel, redeemBox, redeemResultLabel);
    }

    /**
     * Handles point redemption.
     */
    private void handleRedeem() {
        String pointsText = redeemPointsField.getText();

        if (pointsText == null || pointsText.trim().isEmpty()) {
            redeemResultLabel.setText("Please enter points to redeem");
            redeemResultLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        try {
            int pointsToRedeem = Integer.parseInt(pointsText);

            if (pointsToRedeem <= 0) {
                redeemResultLabel.setText("Please enter a positive number");
                redeemResultLabel.setStyle("-fx-text-fill: #ff6b6b;");
                return;
            }

            if (pointsToRedeem > membershipCard.getPoints()) {
                redeemResultLabel.setText("Insufficient points!");
                redeemResultLabel.setStyle("-fx-text-fill: #ff6b6b;");
                return;
            }

            double discount = membershipCard.redeemPoints(pointsToRedeem);
            redeemResultLabel.setText(String.format("Redeemed! You got ₱%.2f discount", discount));
            redeemResultLabel.setStyle("-fx-text-fill: #90EE90;");

            // Refresh display
            refresh();
            redeemPointsField.clear();

        } catch (NumberFormatException e) {
            redeemResultLabel.setText("Invalid number");
            redeemResultLabel.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    /**
     * Refreshes the display with current card information.
     */
    public void refresh() {
        pointsLabel.setText(membershipCard.getPoints() + " pts");
        discountLabel.setText("= ₱" + membershipCard.getDiscount() + " discount");
        pointsProgressBar.setProgress((membershipCard.getPoints() % 100) / 100.0);
    }

    public MembershipCard getMembershipCard() {
        return membershipCard;
    }
}