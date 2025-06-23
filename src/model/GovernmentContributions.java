/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author rejoice
 */
public class GovernmentContributions {
    private int contributionId;
    private int employeeId;
    private double sss;
    private double philhealth;
    private double pagibig;
    private double tax;

    // Constructors
    public GovernmentContributions() {}

    public GovernmentContributions(int employeeId, double sss, double philhealth, double pagibig, double tax) {
        this.employeeId = employeeId;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.tax = tax;
    }

    // Getters and Setters
    public int getContributionId() {
        return contributionId;
    }

    public void setContributionId(int contributionId) {
        this.contributionId = contributionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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

    // REMOVED: getTin() method that was throwing UnsupportedOperationException
    // If TIN is needed, add it as a proper field

    public double getTotalContributions() {
        return sss + philhealth + pagibig + tax;
    }

    @Override
    public String toString() {
        return "GovernmentContributions{" +
                "contributionId=" + contributionId +
                ", employeeId=" + employeeId +
                ", sss=" + sss +
                ", philhealth=" + philhealth +
                ", pagibig=" + pagibig +
                ", tax=" + tax +
                ", total=" + getTotalContributions() +
                '}';
    }
}