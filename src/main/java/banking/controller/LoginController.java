package banking.controller;

import banking.BankStaff;
import banking.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("Customer", "Bank Staff");
        roleComboBox.setValue("Customer");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String role = roleComboBox.getValue();
        String userInput = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userInput.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both User ID and Password.");
            return;
        }

        try {
            if ("Bank Staff".equals(role)) {
                BankStaff matchedStaff = BankDataStore.getStaffMembers().stream()
                        .filter(s -> s.getStaffId().equalsIgnoreCase(userInput)
                                || s.getFirstName().equalsIgnoreCase(userInput))
                        .findFirst()
                        .orElse(null);

                if (matchedStaff != null && "1234".equals(password)) {
                    BankDataStore.setLoggedInStaff(matchedStaff);
                    switchScene(event, "/banking/view/StaffView.fxml", "Bank Staff Portal");
                } else {
                    messageLabel.setText("Invalid staff login (try STF-001 or Kabo / 1234).");
                }
            } else {
                Customer matchedCustomer = BankDataStore.getCustomers().stream()
                        .filter(c -> c.getCustomerId().equalsIgnoreCase(userInput)
                                || c.getFirstName().equalsIgnoreCase(userInput))
                        .findFirst()
                        .orElse(null);

                if (matchedCustomer != null && "1234".equals(password)) {
                    BankDataStore.setLoggedInCustomer(matchedCustomer);
                    switchScene(event, "/banking/view/AccountView.fxml", "Customer Dashboard");
                } else {
                    messageLabel.setText("Invalid customer login (try CUST-001 or Thabo / 1234).");
                }
            }
        } catch (Exception e) {
            messageLabel.setText("Error loading screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
