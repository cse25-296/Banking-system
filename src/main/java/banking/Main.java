package banking;

import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        Customer customer = new Customer(
                "C001",
                "Ada",
                "Lovelace",
                "1 Example Street",
                "+267 12345678",
                "ada@example.com");

        Account savings = new SavingsAccount(
                "A001", new BigDecimal("1000.00"), "Gaborone");
        customer.addAccount(savings);

        savings.deposit(new BigDecimal("250.00"));
        savings.withdraw(new BigDecimal("100.00"));

        System.out.println("Customer: "
                + customer.getFirstName() + " " + customer.getSurname());
        System.out.println("Account: " + savings.getAccountNumber());
        System.out.println("Balance: BWP " + savings.getBalance());
        System.out.println("Monthly interest: BWP "
                + savings.calculateMonthlyInterest());
    }
}
