package banking;

public class BankStaff {

    private final String staffId;
    private final String firstName;
    private final String surname;
    private final String role;
    private final String branch;

    public BankStaff(String staffId,
                     String firstName,
                     String surname,
                     String role,
                     String branch) {
        this.staffId = requireValue(staffId, "Staff ID");
        this.firstName = requireValue(firstName, "First name");
        this.surname = requireValue(surname, "Surname");
        this.role = requireValue(role, "Role");
        this.branch = requireValue(branch, "Branch");
    }

    private static String requireValue(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public String getBranch() {
        return branch;
    }
}
