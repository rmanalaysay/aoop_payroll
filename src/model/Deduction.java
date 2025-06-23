package model;

/**
 * Model class representing payroll deductions
 * @author rejoice
 */
public class Deduction {
    private int deductionId;
    private int employeeId;
    private String type; // "Late", "Undertime", "UnpaidLeave"
    private double amount;
    private String description;

    // Constructors
    public Deduction() {}

    public Deduction(int employeeId, String type, double amount) {
        this.employeeId = employeeId;
        this.type = type;
        this.amount = amount;
    }

    // Getters and Setters with validation
    public int getDeductionId() {
        return deductionId;
    }

    public void setDeductionId(int deductionId) {
        this.deductionId = deductionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        this.employeeId = employeeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        // Validate against allowed types
        if (!isValidType(type)) {
            throw new IllegalArgumentException("Invalid deduction type: " + type);
        }
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Utility methods
    private boolean isValidType(String type) {
        return "Late".equalsIgnoreCase(type) || 
               "Undertime".equalsIgnoreCase(type) || 
               "UnpaidLeave".equalsIgnoreCase(type);
    }

    @Override
    public String toString() {
        return "Deduction{" +
                "deductionId=" + deductionId +
                ", employeeId=" + employeeId +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }
}