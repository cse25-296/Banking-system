package banking;

public class CompanyCustomer extends Customer {
    private String companyName;
    private String businessRegistrationNumber;
    private String taxIdNumber;

    public CompanyCustomer(String customerId, String companyName,
                           String businessRegistrationNumber, String taxIdNumber,
                           String address, String phone, String email) {
        super(customerId, companyName, "(Company)", address, phone, email);
        this.companyName = companyName;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.taxIdNumber = taxIdNumber;
    }

    public String getCompanyName() { return companyName; }
    public String getBusinessRegistrationNumber() { return businessRegistrationNumber; }
    public String getTaxIdNumber() { return taxIdNumber; }
}
