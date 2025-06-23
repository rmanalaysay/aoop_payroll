
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private LocalDate birthdate; // CHANGED: From String to LocalDate
    private String address;
    private String contactInfo;
    private String sssNumber;
    private String philhealthNumber;
    private String pagibigNumber;
    private String tinNumber;
    private int employmentStatusId;
    private int positionId;
    private int supervisorId;

    // Constructors
    public Employee() {}

    public Employee(String firstName, String lastName, LocalDate birthdate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        this.firstName = firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        this.lastName = lastName.trim();
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        if (birthdate == null) {
            throw new IllegalArgumentException("Birthdate cannot be null");
        }
        if (birthdate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate cannot be in the future");
        }
        this.birthdate = birthdate;
    }

    // Backward compatibility method for String birthdate
    public String getBirthday() {
        return birthdate != null ? birthdate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    public void setBirthday(String birthday) {
        if (birthday != null && !birthday.trim().isEmpty()) {
            this.birthdate = LocalDate.parse(birthday);
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo != null ? contactInfo.trim() : null;
    }

    public String getSssNumber() {
        return sssNumber;
    }

    public void setSssNumber(String sssNumber) {
        this.sssNumber = sssNumber != null ? sssNumber.trim() : null;
    }

    public String getPhilhealthNumber() {
        return philhealthNumber;
    }

    public void setPhilhealthNumber(String philhealthNumber) {
        this.philhealthNumber = philhealthNumber != null ? philhealthNumber.trim() : null;
    }

    public String getPagibigNumber() {
        return pagibigNumber;
    }

    public void setPagibigNumber(String pagibigNumber) {
        this.pagibigNumber = pagibigNumber != null ? pagibigNumber.trim() : null;
    }

    public String getTinNumber() {
        return tinNumber;
    }

    public void setTinNumber(String tinNumber) {
        this.tinNumber = tinNumber != null ? tinNumber.trim() : null;
    }

    public int getEmploymentStatusId() {
        return employmentStatusId;
    }

    public void setEmploymentStatusId(int employmentStatusId) {
        this.employmentStatusId = employmentStatusId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    // Utility methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        if (birthdate == null) return 0;
        return LocalDate.now().getYear() - birthdate.getYear();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", fullName='" + getFullName() + '\'' +
                ", birthdate=" + birthdate +
                ", employmentStatusId=" + employmentStatusId +
                ", positionId=" + positionId +
                '}';
    }
}