package banking.dao;

import banking.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void saveCustomer(Customer customer) throws Exception {
        String sql = "INSERT INTO customers (customer_id, category, first_name, surname, id_or_reg_number, tax_id, address, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, customer.getCustomerId());
            if (customer instanceof CompanyCustomer cc) {
                ps.setString(2, "CompanyCustomer");
                ps.setString(3, cc.getCompanyName());
                ps.setString(4, cc.getSurname());
                ps.setString(5, cc.getBusinessRegistrationNumber());
                ps.setString(6, cc.getTaxIdNumber());
            } else if (customer instanceof RegularCustomer rc) {
                ps.setString(2, "RegularCustomer");
                ps.setString(3, rc.getFirstName());
                ps.setString(4, rc.getSurname());
                ps.setString(5, rc.getIdNumber());
                ps.setString(6, null);
            } else {
                ps.setString(2, "Customer");
                ps.setString(3, customer.getFirstName());
                ps.setString(4, customer.getSurname());
                ps.setString(5, null);
                ps.setString(6, null);
            }
            ps.setString(7, customer.getAddress());
            ps.setString(8, customer.getPhone());
            ps.setString(9, customer.getEmail());
            ps.executeUpdate();
        }
    }

    public Customer findByIdOrFirstName(String query) throws Exception {
        String sql = "SELECT * FROM customers WHERE LOWER(customer_id) = LOWER(?) OR LOWER(first_name) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.setString(2, query);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToCustomer(rs);
                }
            }
        }
        return null;
    }

    public List<Customer> findAllCustomers() throws Exception {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToCustomer(rs));
            }
        }
        return list;
    }

    public BankStaff findStaffByIdOrFirstName(String query) throws Exception {
        String sql = "SELECT * FROM bank_staff WHERE LOWER(staff_id) = LOWER(?) OR LOWER(first_name) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.setString(2, query);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new BankStaff(
                            rs.getString("staff_id"),
                            rs.getString("first_name"),
                            rs.getString("surname"),
                            rs.getString("role"),
                            rs.getString("branch")
                    );
                }
            }
        }
        return null;
    }

    private Customer mapRowToCustomer(ResultSet rs) throws Exception {
        String id = rs.getString("customer_id");
        String category = rs.getString("category");
        Customer c;
        if ("CompanyCustomer".equals(category)) {
            c = new CompanyCustomer(id, rs.getString("first_name"), rs.getString("id_or_reg_number"),
                    rs.getString("tax_id"), rs.getString("address"), rs.getString("phone"), rs.getString("email"));
        } else {
            c = new RegularCustomer(id, rs.getString("first_name"), rs.getString("surname"),
                    rs.getString("address"), rs.getString("phone"), rs.getString("email"),
                    rs.getString("id_or_reg_number"), "N/A");
        }
        // Load linked accounts from AccountDAO
        AccountDAO accountDAO = new AccountDAO();
        for (Account acc : accountDAO.findByCustomerId(id)) {
            c.addAccount(acc);
        }
        return c;
    }
}

