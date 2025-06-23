package dao;

import util.DBConnection;
import model.LeaveRequest;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object for Leave Request operations
 * @author rejoice
 */
public class LeaveRequestDAO {
    private static final Logger logger = Logger.getLogger(LeaveRequestDAO.class.getName());
    
    /**
     * Retrieves all approved leave requests for a specific employee
     * @param empId Employee ID
     * @return List of approved leave requests
     */
    public List<LeaveRequest> getApprovedLeavesByEmployeeId(int empId) {
        if (empId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        
        List<LeaveRequest> list = new ArrayList<>();
        String query = "SELECT * FROM leave_request WHERE employee_id = ? AND status = 'Approved' ORDER BY start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveRequest lr = mapResultSetToLeaveRequest(rs);
                    list.add(lr);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving approved leaves for employee ID: " + empId, ex);
            throw new RuntimeException("Failed to retrieve approved leave requests", ex);
        }

        return list;
    }

    /**
     * Retrieves approved leave requests for an employee within a date range
     * @param employeeId Employee ID
     * @param periodStart Start date of the period
     * @param periodEnd End date of the period
     * @return List of approved leave requests within the date range
     */
    public List<LeaveRequest> getApprovedLeavesByEmployeeIdAndDateRange(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period start and end dates cannot be null");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Period start date cannot be after end date");
        }
        
        List<LeaveRequest> list = new ArrayList<>();
        String query = """
            SELECT * FROM leave_request 
            WHERE employee_id = ? AND status = 'Approved' 
            AND ((start_date >= ? AND start_date <= ?) 
                 OR (end_date >= ? AND end_date <= ?)
                 OR (start_date <= ? AND end_date >= ?))
            ORDER BY start_date DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            Date startDate = Date.valueOf(periodStart);
            Date endDate = Date.valueOf(periodEnd);
            
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            stmt.setDate(4, startDate);
            stmt.setDate(5, endDate);
            stmt.setDate(6, startDate);
            stmt.setDate(7, endDate);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveRequest lr = mapResultSetToLeaveRequest(rs);
                    list.add(lr);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving approved leaves for employee ID: " + employeeId + 
                      " between dates: " + periodStart + " and " + periodEnd, ex);
            throw new RuntimeException("Failed to retrieve approved leave requests", ex);
        }

        return list;
    }
    
    /**
     * Inserts a new leave request
     * @param leaveRequest Leave request to insert
     * @return Generated leave request ID
     */
    public int insertLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            throw new IllegalArgumentException("Leave request cannot be null");
        }
        
        String query = "INSERT INTO leave_request (employee_id, leave_type, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, leaveRequest.getEmployeeId());
            stmt.setString(2, leaveRequest.getLeaveType());
            stmt.setDate(3, leaveRequest.getStartDate());
            stmt.setDate(4, leaveRequest.getEndDate());
            stmt.setString(5, leaveRequest.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating leave request failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    leaveRequest.setLeaveId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating leave request failed, no ID obtained.");
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error inserting leave request", ex);
            throw new RuntimeException("Failed to insert leave request", ex);
        }
    }
    
    /**
     * Updates the status of a leave request
     * @param leaveId Leave request ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateLeaveStatus(int leaveId, String status) {
        if (leaveId <= 0) {
            throw new IllegalArgumentException("Leave ID must be positive");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        String query = "UPDATE leave_request SET status = ? WHERE leave_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, leaveId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating leave request status", ex);
            throw new RuntimeException("Failed to update leave request status", ex);
        }
    }
    
    /**
     * Maps ResultSet to LeaveRequest object
     * @param rs ResultSet from database
     * @return LeaveRequest object
     * @throws SQLException if database access error occurs
     */
    private LeaveRequest mapResultSetToLeaveRequest(ResultSet rs) throws SQLException {
        LeaveRequest lr = new LeaveRequest();
        lr.setLeaveId(rs.getInt("leave_id"));
        lr.setEmployeeId(rs.getInt("employee_id"));
        lr.setLeaveType(rs.getString("leave_type"));
        lr.setStartDate(rs.getDate("start_date"));
        lr.setEndDate(rs.getDate("end_date"));
        lr.setStatus(rs.getString("status"));
        return lr;
    }
}