
package banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private String transactionId;
    private String accountNumber;
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private LocalDateTime transactionDate;

    public Transaction(String transactionId,
                       String accountNumber,
                       String transactionType,
                       BigDecimal amount,
                       BigDecimal balanceAfter) {

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
