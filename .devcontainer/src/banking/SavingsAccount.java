
package banking;

import java.math.BigDecimal;

public class SavingsAccount extends Account
        implements InterestBearing {

    private static final BigDecimal INTEREST_RATE =
            new BigDecimal("0.0005");

    public SavingsAccount(String accountNumber,
                          BigDecimal initialBalance,
                          String branch) {

        super(accountNumber, initialBalance, branch);
    }

    @Override
    public void withdraw(BigDecimal amount) {
        throw new UnsupportedOperationException(
                "Withdrawals are not allowed from a Savings Account.");
    }

    @Override
    public BigDecimal calculateMonthlyInterest() {
        ensureActive();

        return getBalance()
                .multiply(INTEREST_RATE)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
