package banking;

public class RegularCustomer extends Customer {
    private String idNumber;
    private String dateOfBirth;

    public RegularCustomer(String customerId, String firstName, String surname,
                           String address, String phone, String email,
                           String idNumber, String dateOfBirth) {
        super(customerId, firstName, surname, address, phone, email);
        this.idNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public String getIdNumber() { return idNumber; }
    public String getDateOfBirth() { return dateOfBirth; }
}
