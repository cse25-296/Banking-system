
package banking;

import java.math.BigDecimal;

public class ChequeAccount extends Account {

    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber,
                         BigDecimal initialBalance,
                         String branch,
                         String employerName,
                         String employerAddress) {

        super(accountNumber, initialBalance, branch);

        if (employerName == null || employerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Employer company name is required.");
        }

        if (employerAddress == null || employerAddress.isBlank()) {
            throw new IllegalArgumentException(
                    "Employer address is required.");
        }

        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    @Override
    public BigDecimal calculateMonthlyInterest() {
        ensureActive();
        return BigDecimal.ZERO;
    }
}
