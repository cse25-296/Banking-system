package banking.controller;

import banking.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BankDataStore {
    private static final List<Customer> customers = new ArrayList<>();
    private static final List<BankStaff> staffMembers = new ArrayList<>();
    private static final List<Transaction> transactions = new ArrayList<>();
    private static Customer loggedInCustomer;
    private static BankStaff loggedInStaff;

    static {
        // Sample Bank Staff (Login: STF-001 or Kabo / 1234)
        staffMembers.add(new BankStaff("STF-001", "Kabo", "Motsumi", "Branch Officer", "Main Mall Branch"));

        // Sample Regular Customer (Login: CUST-001 or Thabo / 1234)
        RegularCustomer thabo = new RegularCustomer(
                "CUST-001", "Thabo", "Khumalo", "Plot 123, Gaborone",
                "71234567", "thabo@example.bw", "987654321", "1998-05-14"
        );
        thabo.addAccount(new SavingsAccount("SAV-1001", new BigDecimal("1500.00"), "Main Mall Branch"));
        thabo.addAccount(new InvestmentAccount("INV-2001", new BigDecimal("5000.00"), "Main Mall Branch"));
        thabo.addAccount(new ChequeAccount("CHQ-3001", new BigDecimal("3200.00"), "Airport Junction", "Botswana Accountancy College", "Fairgrounds, Gaborone"));
        customers.add(thabo);

        // Sample Company Customer (Login: CUST-002 or Kalahari / 1234)
        CompanyCustomer company = new CompanyCustomer(
                "CUST-002", "Kalahari Tech Pty", "BW000012345", "TIN-998877",
                "CBD Plot 543, Gaborone", "3901234", "accounts@kalaharitech.co.bw"
        );
        company.addAccount(new InvestmentAccount("INV-2002", new BigDecimal("15000.00"), "CBD Branch"));
        customers.add(company);
    }

    public static List<Customer> getCustomers() { return customers; }
    public static void addCustomer(Customer customer) { customers.add(customer); }
    public static List<BankStaff> getStaffMembers() { return staffMembers; }
    public static List<Transaction> getAllTransactions() { return transactions; }

    public static Account findAccountByNumber(String accountNumber) {
        for (Customer c : customers) {
            for (Account acc : c.getAccounts()) {
                if (acc.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                    return acc;
                }
            }
        }
        return null;
    }

    public static List<Transaction> getTransactionsForAccount(String accountNumber) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                result.add(t);
            }
        }
        return result;
    }

    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public static Customer getLoggedInCustomer() { return loggedInCustomer; }
    public static void setLoggedInCustomer(Customer customer) { loggedInCustomer = customer; }

    public static BankStaff getLoggedInStaff() { return loggedInStaff; }
    public static void setLoggedInStaff(BankStaff staff) { loggedInStaff = staff; }
}
