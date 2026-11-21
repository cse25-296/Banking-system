package banking.controller;

import banking.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.UUID;

public class StaffController {
    @FXML private ComboBox<String> customerCategoryBox;
    @FXML private TextField firstNameField;
    @FXML private TextField surnameField;
    @FXML private TextField idNumberField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> accountTypeBox;
    @FXML private TextField branchField;
    @FXML private TextField initialDepositField;
    @FXML private TextField companyNameField;
    @FXML private TextField companyAddressField;
    @FXML private Label statusLabel;

    @FXML private TextField manageAccountField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label manageStatusLabel;
    @FXML private TextArea reportOutputArea;

    @FXML
    public void initialize() {
        customerCategoryBox.getItems().addAll("Regular Customer", "Company Customer");
        customerCategoryBox.setValue("Regular Customer");

        accountTypeBox.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
        accountTypeBox.setValue("Savings Account");

        statusComboBox.getItems().addAll("ACTIVE", "SUSPENDED", "CLOSED");
        statusComboBox.setValue("ACTIVE");
    }

    @FXML
    public void handleOpenAccount() {
        try {
            String category = customerCategoryBox.getValue();
            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String idNum = idNumberField.getText().trim();
            String address = addressField.getText().trim();
            String type = accountTypeBox.getValue();
            String branch = branchField.getText().trim();

            if (firstName.isEmpty() || surname.isEmpty()) {
                statusLabel.setText("Name and Surname / Registration No are required.");
                return;
            }

            BigDecimal deposit = new BigDecimal(initialDepositField.getText().trim());
            String accNum = "ACC-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            Account newAccount;

            if ("Investment Account".equals(type)) {
                newAccount = new InvestmentAccount(accNum, deposit, branch);
            } else if ("Cheque Account".equals(type)) {
                newAccount = new ChequeAccount(
                        accNum, deposit, branch,
                        companyNameField.getText().trim(),
                        companyAddressField.getText().trim()
                );
            } else {
                newAccount = new SavingsAccount(accNum, deposit, branch);
            }

            String custId = "CUST-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            Customer customer;
            if ("Company Customer".equals(category)) {
                customer = new CompanyCustomer(custId, firstName, surname, idNum, address, "N/A", "N/A");
            } else {
                customer = new RegularCustomer(custId, firstName, surname, address, "N/A", "N/A", idNum, "N/A");
            }

            customer.addAccount(newAccount);
            BankDataStore.addCustomer(customer);

            statusLabel.setText("Created " + category + " (" + custId + ") with " + type + " (" + accNum + ")!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric initial deposit.");
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void handleUpdateAccountStatus() {
        String accNum = manageAccountField.getText().trim();
        String newStatus = statusComboBox.getValue();
        Account acc = BankDataStore.findAccountByNumber(accNum);
        if (acc == null) {
            manageStatusLabel.setText("Account '" + accNum + "' not found.");
            return;
        }
        acc.setStatus(newStatus);
        manageStatusLabel.setText("Account " + acc.getAccountNumber() + " status updated to " + newStatus + ".");
    }

    @FXML
    public void handleGenerateCustomerReport() {
        StringBuilder sb = new StringBuilder("=== CUSTOMER & ACCOUNT AUDIT REPORT ===\n\n");
        for (Customer c : BankDataStore.getCustomers()) {
            sb.append(String.format("Customer: %s %s [%s] (%s)%n",
                    c.getFirstName(), c.getSurname(), c.getCustomerId(), c.getClass().getSimpleName()));
            for (Account acc : c.getAccounts()) {
                sb.append(String.format("   -> %s | %s | Branch: %s | Status: %s | Balance: BWP %s%n",
                        acc.getAccountNumber(),
                        acc.getClass().getSimpleName(),
                        acc.getBranch(),
                        acc.getStatus(),
                        acc.getBalance().toPlainString()));
            }
            sb.append("\n");
        }
        reportOutputArea.setText(sb.toString());
    }

    @FXML
    public void handleGenerateTransactionReport() {
        StringBuilder sb = new StringBuilder("=== SYSTEM TRANSACTION AUDIT REPORT ===\n\n");
        if (BankDataStore.getAllTransactions().isEmpty()) {
            sb.append("No transactions have been processed in this session yet.\n");
        } else {
            for (Transaction t : BankDataStore.getAllTransactions()) {
                sb.append(String.format("[%s] %s | Account: %s | Type: %s | Amount: BWP %s | Bal After: BWP %s%n",
                        t.getTransactionDate().toLocalDate(),
                        t.getTransactionId(),
                        t.getAccountNumber(),
                        t.getTransactionType(),
                        t.getAmount().toPlainString(),
                        t.getBalanceAfter().toPlainString()));
            }
        }
        reportOutputArea.setText(sb.toString());
    }

    @FXML
    public void handleApplyAllMonthlyInterest() {
        int count = 0;
        for (Customer c : BankDataStore.getCustomers()) {
            for (Account acc : c.getAccounts()) {
                if ("ACTIVE".equals(acc.getStatus()) && acc instanceof InterestBearing) {
                    BigDecimal interest = acc.calculateMonthlyInterest();
                    if (interest.compareTo(BigDecimal.ZERO) > 0) {
                        acc.deposit(interest);
                        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                        BankDataStore.addTransaction(new Transaction(txId, acc.getAccountNumber(), "MONTHLY_INTEREST", interest, acc.getBalance()));
                        count++;
                    }
                }
            }
        }
        manageStatusLabel.setText("Applied monthly interest to " + count + " eligible active accounts.");
        handleGenerateCustomerReport();
    }

    @FXML
    public void handleLogout(ActionEvent event) throws Exception {
        BankDataStore.setLoggedInStaff(null);
        Parent root = FXMLLoader.load(getClass().getResource("/banking/view/LoginView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("CSE202 Banking System");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
