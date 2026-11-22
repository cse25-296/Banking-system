package banking.dao;

import banking.Transaction;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void saveTransaction(Transaction tx) throws Exception {
        String sql = "INSERT INTO transactions (transaction_id, account_number, transaction_type, amount, balance_after, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tx.getTransactionId());
            ps.setString(2, tx.getAccountNumber());
            ps.setString(3, tx.getTransactionType());
            ps.setString(4, tx.getAmount().toPlainString());
            ps.setString(5, tx.getBalanceAfter().toPlainString());
            ps.setString(6, tx.getTransactionDate().toString());
            ps.executeUpdate();
        }
    }

    public List<String> getFormattedHistoryForAccount(String accountNumber) throws Exception {
        List<String> lines = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE LOWER(account_number) = LOWER(?) ORDER BY rowid DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lines.add(String.format("[%s] %s: BWP %s | Balance After: BWP %s (ID: %s)",
                            rs.getString("transaction_date").substring(0, 10),
                            rs.getString("transaction_type"),
                            rs.getString("amount"),
                            rs.getString("balance_after"),
                            rs.getString("transaction_id")));
                }
            }
        }
        return lines;
    }

    public List<String> getAllFormattedTransactions() throws Exception {
        List<String> lines = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY rowid DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lines.add(String.format("[%s] %s | Account: %s | Type: %s | Amount: BWP %s | Bal After: BWP %s",
                        rs.getString("transaction_date").substring(0, 10),
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        rs.getString("transaction_type"),
                        rs.getString("amount"),
                        rs.getString("balance_after")));
            }
        }
        return lines;
    }
}
