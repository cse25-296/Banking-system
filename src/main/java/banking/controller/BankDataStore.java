package banking.controller;

import banking.BankStaff;
import banking.Customer;

public class BankDataStore {
    private static Customer loggedInCustomer;
    private static BankStaff loggedInStaff;

    public static Customer getLoggedInCustomer() { return loggedInCustomer; }
    public static void setLoggedInCustomer(Customer customer) { loggedInCustomer = customer; }

    public static BankStaff getLoggedInStaff() { return loggedInStaff; }
    public static void setLoggedInStaff(BankStaff staff) { loggedInStaff = staff; }
}
