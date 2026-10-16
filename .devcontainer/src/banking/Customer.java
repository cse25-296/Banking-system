
package banking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer {

    private String customerId;
    private String firstName;
    private String surname;
    private String address;
    private String phone;
    private String email;

    private List<Account> accounts;

    public Customer(String customerId,
                    String firstName,
                    String surname,
                    String address,
                    String phone,
                    String email) {

        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID is required.");
        }

        this.customerId = customerId;
        this.firstName = firstName;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        for (Account existing : accounts) {
            if (existing.getAccountNumber()
                    .equals(account.getAccountNumber())) {
                throw new IllegalArgumentException(
                        "This account is already linked to the customer.");
            }
        }

        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return Collections.unmodifiableList(accounts);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void updateContactDetails(String address,
                                     String phone,
                                     String email) {
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}
