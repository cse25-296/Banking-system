
package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvestmentAccount extends Account
        implements InterestBearing {

    private static final BigDecimal MINIMUM_OPENING_DEPOSIT =
            new BigDecimal("500.00");

    private static final BigDecimal INTEREST_RATE =
            new BigDecimal("0.05");

    public InvestmentAccount(String accountNumber,
                             BigDecimal initialBalance,
                             String branch) {

        super(accountNumber, initialBalance, branch);

        if (initialBalance.compareTo(MINIMUM_OPENING_DEPOSIT) < 0) {
            throw new IllegalArgumentException(
                    "Investment Account requires an opening deposit "
                    + "of at least BWP 500.00.");
        }
    }

    @Override
    public BigDecimal calculateMonthlyInterest() {
        ensureActive();

        return getBalance()
                .multiply(INTEREST_RATE)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
