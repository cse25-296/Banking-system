
package banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private final String transactionId;
    private final String accountNumber;
    private final String transactionType;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final LocalDateTime transactionDate;

    public Transaction(String transactionId,
                       String accountNumber,
                       String transactionType,
                       BigDecimal amount,
                       BigDecimal balanceAfter) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction ID is required.");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Account number is required.");
        }
        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction type is required.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be greater than zero.");
        }
        if (balanceAfter == null
                || balanceAfter.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Balance after transaction cannot be negative.");
        }

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transactionDate = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId
                + "\nAccount Number: " + accountNumber
                + "\nType: " + transactionType
                + "\nAmount: BWP " + amount
                + "\nBalance After: BWP " + balanceAfter
                + "\nDate: " + transactionDate;
    }
}
