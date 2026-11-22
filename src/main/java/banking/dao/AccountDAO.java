package banking.dao;

import banking.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void saveAccount(Account account, String customerId) throws Exception {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, branch, status, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getAccountNumber());
            ps.setString(2, customerId);
            ps.setString(3, account.getClass().getSimpleName());
            ps.setString(4, account.getBalance().toPlainString());
            ps.setString(5, account.getBranch());
            ps.setString(6, account.getStatus());

            if (account instanceof ChequeAccount chq) {
                ps.setString(7, chq.getEmployerName());
                ps.setString(8, chq.getEmployerAddress());
            } else {
                ps.setString(7, null);
                ps.setString(8, null);
            }
            ps.executeUpdate();
        }
    }

    public void updateBalance(String accountNumber, BigDecimal newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newBalance.toPlainString());
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    public void updateStatus(String accountNumber, String newStatus) throws Exception {
        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, accountNumber);
            ps.executeUpdate();
        }
    }

    public Account findByAccountNumber(String accountNumber) throws Exception {
        String sql = "SELECT * FROM accounts WHERE LOWER(account_number) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAccount(rs);
                }
            }
        }
        return null;
    }

    public List<Account> findByCustomerId(String customerId) throws Exception {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAccount(rs));
                }
            }
        }
        return list;
    }

    private Account mapRowToAccount(ResultSet rs) throws Exception {
        String accNum = rs.getString("account_number");
        String type = rs.getString("account_type");
        BigDecimal balance = new BigDecimal(rs.getString("balance"));
        String branch = rs.getString("branch");
        String status = rs.getString("status");

        Account acc;
        if ("InvestmentAccount".equals(type)) {
            acc = new InvestmentAccount(accNum, balance, branch);
        } else if ("ChequeAccount".equals(type)) {
            acc = new ChequeAccount(accNum, balance, branch,
                    rs.getString("employer_name"), rs.getString("employer_address"));
        } else {
            acc = new SavingsAccount(accNum, balance, branch);
        }
        acc.setStatus(status);
        return acc;
    }
}
