/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author rejoice
 */
public class LeaveRequest {
    private int leaveId;
    private int employeeId;
    private java.sql.Date startDate;
    private java.sql.Date endDate;
    private String leaveType;
    private String status;

    // Constructors
    public LeaveRequest() {}

    public LeaveRequest(int employeeId, Date startDate, Date endDate, String leaveType) {
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.status = "Pending"; // Default status
    }

    // Getters and Setters
    public int getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(int leaveId) {
        this.leaveId = leaveId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getStartDate() {
        return startDate;
    }

    // FIXED: Was assigning to itself
    public void setStartDate(Date startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }

    // FIXED: Was assigning to itself
    public void setEndDate(Date endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        this.endDate = endDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        if (leaveType == null || leaveType.trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type cannot be null or empty");
        }
        this.leaveType = leaveType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        this.status = status;
    }

    // Utility method to calculate leave days
    public long getLeaveDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        long diffInMillis = endDate.getTime() - startDate.getTime();
        return (diffInMillis / (1000 * 60 * 60 * 24)) + 1; // +1 to include both start and end dates
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "leaveId=" + leaveId +
                ", employeeId=" + employeeId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", leaveType='" + leaveType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}