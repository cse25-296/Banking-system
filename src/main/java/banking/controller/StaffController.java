package banking.controller;

import banking.*;
import banking.dao.AccountDAO;
import banking.dao.CustomerDAO;
import banking.dao.TransactionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
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

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

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

            // Save customer, account, and opening deposit transaction to SQLite
            customerDAO.saveCustomer(customer);
            accountDAO.saveAccount(newAccount, custId);
            if (deposit.compareTo(BigDecimal.ZERO) > 0) {
                String txId = "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                transactionDAO.saveTransaction(new Transaction(txId, accNum, "OPENING_DEPOSIT", deposit, deposit));
            }

            statusLabel.setText("Saved to DB: " + category + " (" + custId + ") with " + type + " (" + accNum + ")!");
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric initial deposit.");
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateAccountStatus() {
        String accNum = manageAccountField.getText().trim();
        String newStatus = statusComboBox.getValue();
        try {
            Account acc = accountDAO.findByAccountNumber(accNum);
            if (acc == null) {
                manageStatusLabel.setText("Account '" + accNum + "' not found in database.");
                return;
            }
            accountDAO.updateStatus(acc.getAccountNumber(), newStatus);
            manageStatusLabel.setText("Database updated: " + acc.getAccountNumber() + " is now " + newStatus + ".");
        } catch (Exception e) {
            manageStatusLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleGenerateCustomerReport() {
        try {
            StringBuilder sb = new StringBuilder("=== CUSTOMER & ACCOUNT AUDIT REPORT (FROM DATABASE) ===\n\n");
            for (Customer c : customerDAO.findAllCustomers()) {
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
        } catch (Exception e) {
            reportOutputArea.setText("Error generating customer report: " + e.getMessage());
        }
    }

    @FXML
    public void handleGenerateTransactionReport() {
        try {
            StringBuilder sb = new StringBuilder("=== SYSTEM TRANSACTION AUDIT REPORT (FROM DATABASE) ===\n\n");
            List<String> lines = transactionDAO.getAllFormattedTransactions();
            if (lines.isEmpty()) {
                sb.append("No transactions recorded in database yet.\n");
            } else {
                sb.append(String.join("\n", lines));
            }
            reportOutputArea.setText(sb.toString());
        } catch (Exception e) {
            reportOutputArea.setText("Error generating transaction report: " + e.getMessage());
        }
    }

    @FXML
    public void handleApplyAllMonthlyInterest() {
        try {
            int count = 0;
            for (Customer c : customerDAO.findAllCustomers()) {
                for (Account acc : c.getAccounts()) {
                    if ("ACTIVE".equals(acc.getStatus()) && acc instanceof InterestBearing) {
                        BigDecimal interest = acc.calculateMonthlyInterest();
                        if (interest.compareTo(BigDecimal.ZERO) > 0) {
                            acc.deposit(interest);
                            accountDAO.updateBalance(acc.getAccountNumber(), acc.getBalance());
                            String txId = "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                            transactionDAO.saveTransaction(new Transaction(txId, acc.getAccountNumber(), "MONTHLY_INTEREST", interest, acc.getBalance()));
                            count++;
                        }
                    }
                }
            }
            manageStatusLabel.setText("Applied & saved monthly interest to " + count + " active accounts.");
            handleGenerateCustomerReport();
        } catch (Exception e) {
            manageStatusLabel.setText("Error applying interest: " + e.getMessage());
        }
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
