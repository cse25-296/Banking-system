package banking.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:banking.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Load and execute schema.sql from src/main/resources/
            InputStream in = DatabaseConnection.class.getResourceAsStream("/schema.sql");
            if (in != null) {
                String sql = new BufferedReader(new InputStreamReader(in))
                        .lines().collect(Collectors.joining("\n"));
                for (String statement : sql.split(";")) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement.trim());
                    }
                }
            }

            // Seed default staff and sample customer if database is brand new
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM bank_staff");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO bank_staff VALUES ('STF-001', 'Kabo', 'Motsumi', 'Branch Officer', 'Main Mall Branch')");
                stmt.executeUpdate("INSERT INTO customers VALUES ('CUST-001', 'RegularCustomer', 'Thabo', 'Khumalo', '987654321', NULL, 'Plot 123, Gaborone', '71234567', 'thabo@example.bw')");
                stmt.executeUpdate("INSERT INTO accounts VALUES ('SAV-1001', 'CUST-001', 'SavingsAccount', '1500.00', 'Main Mall Branch', 'ACTIVE', NULL, NULL)");
                stmt.executeUpdate("INSERT INTO accounts VALUES ('INV-2001', 'CUST-001', 'InvestmentAccount', '5000.00', 'Main Mall Branch', 'ACTIVE', NULL, NULL)");
                stmt.executeUpdate("INSERT INTO accounts VALUES ('CHQ-3001', 'CUST-001', 'ChequeAccount', '3200.00', 'Airport Junction', 'ACTIVE', 'Botswana Accountancy College', 'Fairgrounds, Gaborone')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
