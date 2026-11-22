package banking.controller;

import banking.*;
import banking.dao.AccountDAO;
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

public class AccountController {
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> accountSelector;
    @FXML private Label accountTypeLabel;
    @FXML private Label branchLabel;
    @FXML private Label accountStatusLabel;
    @FXML private Label balanceLabel;
    @FXML private TextField amountField;
    @FXML private TextField targetAccountField;
    @FXML private Label statusLabel;
    @FXML private TextArea transactionHistoryArea;

    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    private Customer currentCustomer;
    private List<Account> accounts;

    @FXML
    public void initialize() {
        currentCustomer = BankDataStore.getLoggedInCustomer();
        if (currentCustomer != null) {
            welcomeLabel.setText("Welcome, " + currentCustomer.getFirstName() + " " + currentCustomer.getSurname()
                    + " (" + currentCustomer.getCustomerId() + ")");
            accounts = currentCustomer.getAccounts();
            for (Account acc : accounts) {
                accountSelector.getItems().add(acc.getAccountNumber() + " (" + acc.getClass().getSimpleName() + ")");
            }
            if (!accounts.isEmpty()) {
                accountSelector.getSelectionModel().selectFirst();
                updateAccountDetails();
            }
        }
    }

    @FXML
    public void handleAccountChange() {
        updateAccountDetails();
    }

    private void updateAccountDetails() {
        Account selected = getSelectedAccount();
        if (selected != null) {
            accountTypeLabel.setText(selected.getClass().getSimpleName());
            branchLabel.setText(selected.getBranch());
            accountStatusLabel.setText(selected.getStatus());
            balanceLabel.setText("BWP " + selected.getBalance().toPlainString());
            statusLabel.setText("");
            refreshTransactionHistory(selected.getAccountNumber());
        }
    }

    private void refreshTransactionHistory(String accountNumber) {
        try {
            List<String> history = transactionDAO.getFormattedHistoryForAccount(accountNumber);
            if (history.isEmpty()) {
                transactionHistoryArea.setText("No transactions recorded yet for " + accountNumber + ".");
            } else {
                transactionHistoryArea.setText(String.join("\n", history));
            }
        } catch (Exception e) {
            transactionHistoryArea.setText("Error loading transaction history: " + e.getMessage());
        }
    }

    private Account getSelectedAccount() {
        int index = accountSelector.getSelectionModel().getSelectedIndex();
        if (index >= 0 && accounts != null && index < accounts.size()) {
            return accounts.get(index);
        }
        return null;
    }

    @FXML
    public void handleDeposit() {
        Account acc = getSelectedAccount();
        if (acc == null) return;
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            acc.deposit(amount);

            accountDAO.updateBalance(acc.getAccountNumber(), acc.getBalance());
            recordTx(acc.getAccountNumber(), "DEPOSIT", amount, acc.getBalance());

            updateAccountDetails();
            statusLabel.setText("Deposited BWP " + amount.toPlainString() + " successfully.");
            amountField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric amount.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleWithdraw() {
        Account acc = getSelectedAccount();
        if (acc == null) return;
        if (acc instanceof SavingsAccount) {
            statusLabel.setText("Withdrawals are strictly prohibited on a Savings Account.");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            acc.withdraw(amount);

            accountDAO.updateBalance(acc.getAccountNumber(), acc.getBalance());
            recordTx(acc.getAccountNumber(), "WITHDRAWAL", amount, acc.getBalance());

            updateAccountDetails();
            statusLabel.setText("Withdrew BWP " + amount.toPlainString() + " successfully.");
            amountField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric amount.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handleTransfer() {
        Account source = getSelectedAccount();
        if (source == null) return;
        if (source instanceof SavingsAccount) {
            statusLabel.setText("Transfers/withdrawals are not allowed from a Savings Account.");
            return;
        }
        String targetNum = targetAccountField.getText().trim();
        if (targetNum.isEmpty()) {
            statusLabel.setText("Enter a target account number to transfer funds.");
            return;
        }
        try {
            Account target = accountDAO.findByAccountNumber(targetNum);
            if (target == null) {
                statusLabel.setText("Target account '" + targetNum + "' was not found.");
                return;
            }
            if (target.getAccountNumber().equalsIgnoreCase(source.getAccountNumber())) {
                statusLabel.setText("Cannot transfer funds to the same account.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            source.withdraw(amount);
            target.deposit(amount);

            // Persist both updated balances and both transaction logs in SQLite
            accountDAO.updateBalance(source.getAccountNumber(), source.getBalance());
            accountDAO.updateBalance(target.getAccountNumber(), target.getBalance());
            recordTx(source.getAccountNumber(), "TRANSFER_OUT -> " + target.getAccountNumber(), amount, source.getBalance());
            recordTx(target.getAccountNumber(), "TRANSFER_IN <- " + source.getAccountNumber(), amount, target.getBalance());

            // Keep in-memory list synced if target belongs to the same logged-in customer
            for (Account localAcc : accounts) {
                if (localAcc.getAccountNumber().equalsIgnoreCase(target.getAccountNumber())) {
                    localAcc.deposit(amount);
                }
            }

            updateAccountDetails();
            statusLabel.setText("Transferred BWP " + amount.toPlainString() + " to " + target.getAccountNumber() + ".");
            amountField.clear();
            targetAccountField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Please enter a valid numeric transfer amount.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML
    public void handlePayInterest() {
        Account acc = getSelectedAccount();
        if (acc == null) return;
        try {
            BigDecimal interest = acc.calculateMonthlyInterest();
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                acc.deposit(interest);

                accountDAO.updateBalance(acc.getAccountNumber(), acc.getBalance());
                recordTx(acc.getAccountNumber(), "INTEREST", interest, acc.getBalance());

                updateAccountDetails();
                statusLabel.setText("Monthly interest of BWP " + interest.toPlainString() + " applied.");
            } else {
                statusLabel.setText("This account type earns BWP 0.00 monthly interest.");
            }
        } catch (IllegalStateException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    private void recordTx(String accNum, String type, BigDecimal amount, BigDecimal balAfter) throws Exception {
        String txId = "TX-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        transactionDAO.saveTransaction(new Transaction(txId, accNum, type, amount, balAfter));
    }

    @FXML
    public void handleLogout(ActionEvent event) throws Exception {
        BankDataStore.setLoggedInCustomer(null);
        Parent root = FXMLLoader.load(getClass().getResource("/banking/view/LoginView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("CSE202 Banking System");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
