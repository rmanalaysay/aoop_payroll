/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;
import java.time.LocalDate;

/**
 *
 * @author rejoice
 */
import java.sql.Date;
import java.time.LocalDate;

public class Payroll {
    private int payrollId;
    private int employeeId;
    private Date periodStart;
    private Date periodEnd;
    private double monthlyRate;
    private int daysWorked;
    private double overtimeHours;
    private double grossPay;
    private double totalDeductions;
    private double netPay;
    
    // Additional payroll components
    private double grossEarnings;
    private double lateDeduction;
    private double undertimeDeduction;
    private double unpaidLeaveDeduction;
    private double overtimePay;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;

    // Constructors
    public Payroll() {}

    public Payroll(int employeeId, Date periodStart, Date periodEnd) {
        this.employeeId = employeeId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Basic Getters and Setters
    public int getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(int payrollId) {
        this.payrollId = payrollId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(Date periodStart) {
        if (periodStart == null) {
            throw new IllegalArgumentException("Period start cannot be null");
        }
        this.periodStart = periodStart;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Date periodEnd) {
        if (periodEnd == null) {
            throw new IllegalArgumentException("Period end cannot be null");
        }
        if (periodStart != null && periodEnd.before(periodStart)) {
            throw new IllegalArgumentException("Period end cannot be before period start");
        }
        this.periodEnd = periodEnd;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(double monthlyRate) {
        if (monthlyRate < 0) {
            throw new IllegalArgumentException("Monthly rate cannot be negative");
        }
        this.monthlyRate = monthlyRate;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(int daysWorked) {
        if (daysWorked < 0) {
            throw new IllegalArgumentException("Days worked cannot be negative");
        }
        this.daysWorked = daysWorked;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(double overtimeHours) {
        if (overtimeHours < 0) {
            throw new IllegalArgumentException("Overtime hours cannot be negative");
        }
        this.overtimeHours = overtimeHours;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public void setGrossPay(double grossPay) {
        if (grossPay < 0) {
            throw new IllegalArgumentException("Gross pay cannot be negative");
        }
        this.grossPay = grossPay;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(double totalDeductions) {
        if (totalDeductions < 0) {
            throw new IllegalArgumentException("Total deductions cannot be negative");
        }
        this.totalDeductions = totalDeductions;
    }

    public double getNetPay() {
        return netPay;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    // Additional component getters and setters - PROPERLY IMPLEMENTED
    public double getGrossEarnings() {
        return grossEarnings;
    }

    public void setGrossEarnings(double grossEarnings) {
        if (grossEarnings < 0) {
            throw new IllegalArgumentException("Gross earnings cannot be negative");
        }
        this.grossEarnings = grossEarnings;
    }

    public double getLateDeduction() {
        return lateDeduction;
    }

    public void setLateDeduction(double lateDeduction) {
        if (lateDeduction < 0) {
            throw new IllegalArgumentException("Late deduction cannot be negative");
        }
        this.lateDeduction = lateDeduction;
    }

    public double getUndertimeDeduction() {
        return undertimeDeduction;
    }

    public void setUndertimeDeduction(double undertimeDeduction) {
        if (undertimeDeduction < 0) {
            throw new IllegalArgumentException("Undertime deduction cannot be negative");
        }
        this.undertimeDeduction = undertimeDeduction;
    }

    public double getUnpaidLeaveDeduction() {
        return unpaidLeaveDeduction;
    }

    public void setUnpaidLeaveDeduction(double unpaidLeaveDeduction) {
        if (unpaidLeaveDeduction < 0) {
            throw new IllegalArgumentException("Unpaid leave deduction cannot be negative");
        }
        this.unpaidLeaveDeduction = unpaidLeaveDeduction;
    }

    public double getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(double overtimePay) {
        if (overtimePay < 0) {
            throw new IllegalArgumentException("Overtime pay cannot be negative");
        }
        this.overtimePay = overtimePay;
    }

    public double getRiceSubsidy() {
        return riceSubsidy;
    }

    public void setRiceSubsidy(double riceSubsidy) {
        if (riceSubsidy < 0) {
            throw new IllegalArgumentException("Rice subsidy cannot be negative");
        }
        this.riceSubsidy = riceSubsidy;
    }

    public double getPhoneAllowance() {
        return phoneAllowance;
    }

    public void setPhoneAllowance(double phoneAllowance) {
        if (phoneAllowance < 0) {
            throw new IllegalArgumentException("Phone allowance cannot be negative");
        }
        this.phoneAllowance = phoneAllowance;
    }

    public double getClothingAllowance() {
        return clothingAllowance;
    }

    public void setClothingAllowance(double clothingAllowance) {
        if (clothingAllowance < 0) {
            throw new IllegalArgumentException("Clothing allowance cannot be negative");
        }
        this.clothingAllowance = clothingAllowance;
    }

    public double getSss() {
        return sss;
    }

    public void setSss(double sss) {
        if (sss < 0) {
            throw new IllegalArgumentException("SSS contribution cannot be negative");
        }
        this.sss = sss;
    }

    public double getPhilhealth() {
        return philhealth;
    }

    public void setPhilhealth(double philhealth) {
        if (philhealth < 0) {
            throw new IllegalArgumentException("PhilHealth contribution cannot be negative");
        }
        this.philhealth = philhealth;
    }

    public double getPagibig() {
        return pagibig;
    }

    public void setPagibig(double pagibig) {
        if (pagibig < 0) {
            throw new IllegalArgumentException("Pag-IBIG contribution cannot be negative");
        }
        this.pagibig = pagibig;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        if (tax < 0) {
            throw new IllegalArgumentException("Tax cannot be negative");
        }
        this.tax = tax;
    }

    // LocalDate compatibility methods
    public void setStartDate(LocalDate startDate) {
        if (startDate != null) {
            this.periodStart = Date.valueOf(startDate);
        }
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate != null) {
            this.periodEnd = Date.valueOf(endDate);
        }
    }

    public LocalDate getStartDateAsLocalDate() {
        return periodStart != null ? periodStart.toLocalDate() : null;
    }

    public LocalDate getEndDateAsLocalDate() {
        return periodEnd != null ? periodEnd.toLocalDate() : null;
    }

    // Calculation methods
    public void calculateGrossPay() {
        this.grossPay = grossEarnings + overtimePay + riceSubsidy + phoneAllowance + clothingAllowance;
    }

    public void calculateTotalDeductions() {
        this.totalDeductions = lateDeduction + undertimeDeduction + unpaidLeaveDeduction + 
                              sss + philhealth + pagibig + tax;
    }

    public void calculateNetPay() {
        calculateGrossPay();
        calculateTotalDeductions();
        this.netPay = grossPay - totalDeductions;
    }

    @Override
    public String toString() {
        return "Payroll{" +
                "payrollId=" + payrollId +
                ", employeeId=" + employeeId +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", grossPay=" + grossPay +
                ", netPay=" + netPay +
                '}';
    }
}
