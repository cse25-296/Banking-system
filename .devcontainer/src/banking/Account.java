
package banking;

import java.math.BigDecimal;

public abstract class Account {

    private String accountNumber;
    private BigDecimal balance;
    private String branch;
    private String status;

    public Account(String accountNumber, BigDecimal initialBalance,
                   String branch) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                "Account number is required.");
        }

        if (initialBalance == null
                || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                "Initial balance cannot be negative.");
        }

        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.branch = branch;
        this.status = "ACTIVE";
    }

    public void deposit(BigDecimal amount) {
        validatePositiveAmount(amount);
        ensureActive();

        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        validatePositiveAmount(amount);
        ensureActive();

        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Insufficient funds.");
        }

        balance = balance.subtract(amount);
    }

    protected void validatePositiveAmount(BigDecimal amount) {
        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero.");
        }
    }

    protected void ensureActive() {
        if (!"ACTIVE".equals(status)) {
            throw new IllegalStateException(
                "Account is not active.");
        }
    }

    public abstract BigDecimal calculateMonthlyInterest();

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null
                || !status.matches("ACTIVE|SUSPENDED|CLOSED")) {
            throw new IllegalArgumentException(
                "Invalid account status.");
        }

        this.status = status;
    }
}
