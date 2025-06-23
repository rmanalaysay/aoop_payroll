package model;

import java.util.Objects;

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

    // Deduction type constants
    public static final String TYPE_LATE = "Late";
    public static final String TYPE_UNDERTIME = "Undertime";
    public static final String TYPE_UNPAID_LEAVE = "UnpaidLeave";

    // Constructors
    public Deduction() {}

    public Deduction(int employeeId, String type, double amount) {
        setEmployeeId(employeeId);
        setType(type);
        setAmount(amount);
    }

    public Deduction(int employeeId, String type, double amount, String description) {
        this(employeeId, type, amount);
        setDescription(description);
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
        this.type = type.trim();
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
        this.description = description != null ? description.trim() : null;
    }

    // Utility methods
    private boolean isValidType(String type) {
        return TYPE_LATE.equalsIgnoreCase(type) || 
               TYPE_UNDERTIME.equalsIgnoreCase(type) || 
               TYPE_UNPAID_LEAVE.equalsIgnoreCase(type);
    }

    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    public String getFormattedAmount() {
        return String.format("%.2f", amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Deduction deduction = (Deduction) obj;
        return deductionId == deduction.deductionId &&
               employeeId == deduction.employeeId &&
               Objects.equals(type, deduction.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deductionId, employeeId, type);
    }

    @Override
    public String toString() {
        return "Deduction{" +
                "deductionId=" + deductionId +
                ", employeeId=" + employeeId +
                ", type='" + type + '\'' +
                ", amount=" + getFormattedAmount() +
                ", description='" + description + '\'' +
                '}';
    }
}