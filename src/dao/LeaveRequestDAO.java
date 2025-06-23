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
     * Retrieves all leave requests for a specific employee
     * @param empId Employee ID
     * @return List of leave requests
     */
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(int empId) {
        if (empId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        
        List<LeaveRequest> list = new ArrayList<>();
        String query = "SELECT * FROM leave_request WHERE employee_id = ? ORDER BY start_date DESC";

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
            logger.log(Level.SEVERE, "Error retrieving leave requests for employee ID: " + empId, ex);
            throw new RuntimeException("Failed to retrieve leave requests", ex);
        }

        return list;
    }

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
        String query = "SELECT * FROM leave_request WHERE employee_id = ? AND status = ? ORDER BY start_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            stmt.setString(2, LeaveRequest.STATUS_APPROVED);
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
            WHERE employee_id = ? AND status = ? 
            AND ((start_date >= ? AND start_date <= ?) 
                 OR (end_date >= ? AND end_date <= ?)
                 OR (start_date <= ? AND end_date >= ?))
            ORDER BY start_date DESC
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, LeaveRequest.STATUS_APPROVED);
            Date startDate = Date.valueOf(periodStart);
            Date endDate = Date.valueOf(periodEnd);
            
            stmt.setDate(3, startDate);
            stmt.setDate(4, endDate);
            stmt.setDate(5, startDate);
            stmt.setDate(6, endDate);
            stmt.setDate(7, startDate);
            stmt.setDate(8, endDate);
            
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
     * Retrieves leave requests by status
     * @param status Leave request status
     * @return List of leave requests with specified status
     */
    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        List<LeaveRequest> list = new ArrayList<>();
        String query = "SELECT * FROM leave_request WHERE status = ? ORDER BY start_date DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LeaveRequest lr = mapResultSetToLeaveRequest(rs);
                    list.add(lr);
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving leave requests by status: " + status, ex);
            throw new RuntimeException("Failed to retrieve leave requests", ex);
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
        if (leaveRequest.getEmployeeId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (leaveRequest.getLeaveType() == null || leaveRequest.getLeaveType().trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type cannot be null or empty");
        }
        
        String query = "INSERT INTO leave_request (employee_id, leave_type, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, leaveRequest.getEmployeeId());
            stmt.setString(2, leaveRequest.getLeaveType());
            stmt.setDate(3, leaveRequest.getStartDate());
            stmt.setDate(4, leaveRequest.getEndDate());
            stmt.setString(5, leaveRequest.getStatus() != null ? leaveRequest.getStatus() : LeaveRequest.STATUS_PENDING);
            
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
            
            stmt.setString(1, status.trim());
            stmt.setInt(2, leaveId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating leave request status", ex);
            throw new RuntimeException("Failed to update leave request status", ex);
        }
    }

    /**
     * Updates a leave request
     * @param leaveRequest Leave request with updated information
     * @return true if update was successful
     */
    public boolean updateLeaveRequest(LeaveRequest leaveRequest) {
        if (leaveRequest == null) {
            throw new IllegalArgumentException("Leave request cannot be null");
        }
        if (leaveRequest.getLeaveId() <= 0) {
            throw new IllegalArgumentException("Leave ID must be positive");
        }
        
        String query = "UPDATE leave_request SET employee_id = ?, leave_type = ?, start_date = ?, end_date = ?, status = ? WHERE leave_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, leaveRequest.getEmployeeId());
            stmt.setString(2, leaveRequest.getLeaveType());
            stmt.setDate(3, leaveRequest.getStartDate());
            stmt.setDate(4, leaveRequest.getEndDate());
            stmt.setString(5, leaveRequest.getStatus());
            stmt.setInt(6, leaveRequest.getLeaveId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating leave request", ex);
            throw new RuntimeException("Failed to update leave request", ex);
        }
    }

    /**
     * Deletes a leave request
     * @param leaveId Leave request ID
     * @return true if deletion was successful
     */
    public boolean deleteLeaveRequest(int leaveId) {
        if (leaveId <= 0) {
            throw new IllegalArgumentException("Leave ID must be positive");
        }
        
        String query = "DELETE FROM leave_request WHERE leave_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, leaveId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting leave request", ex);
            throw new RuntimeException("Failed to delete leave request", ex);
        }
    }

    /**
     * Retrieves leave request by ID
     * @param leaveId Leave request ID
     * @return LeaveRequest object or null if not found
     */
    public LeaveRequest getLeaveRequestById(int leaveId) {
        if (leaveId <= 0) {
            throw new IllegalArgumentException("Leave ID must be positive");
        }
        
        String query = "SELECT * FROM leave_request WHERE leave_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, leaveId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLeaveRequest(rs);
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving leave request by ID: " + leaveId, ex);
            throw new RuntimeException("Failed to retrieve leave request", ex);
        }
        
        return null;
    }

    /**
     * Checks for overlapping leave requests
     * @param employeeId Employee ID
     * @param startDate Start date
     * @param endDate End date
     * @param excludeLeaveId Leave ID to exclude from check (for updates)
     * @return true if overlapping leave exists
     */
    public boolean hasOverlappingLeave(int employeeId, LocalDate startDate, LocalDate endDate, Integer excludeLeaveId) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        
        String query = """
            SELECT COUNT(*) FROM leave_request 
            WHERE employee_id = ? AND status = ? 
            AND ((start_date >= ? AND start_date <= ?) 
                 OR (end_date >= ? AND end_date <= ?)
                 OR (start_date <= ? AND end_date >= ?))
            """ + (excludeLeaveId != null ? " AND leave_id != ?" : "");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, employeeId);
            stmt.setString(paramIndex++, LeaveRequest.STATUS_APPROVED);
            Date start = Date.valueOf(startDate);
            Date end = Date.valueOf(endDate);
            
            stmt.setDate(paramIndex++, start);
            stmt.setDate(paramIndex++, end);
            stmt.setDate(paramIndex++, start);
            stmt.setDate(paramIndex++, end);
            stmt.setDate(paramIndex++, start);
            stmt.setDate(paramIndex++, end);
            
            if (excludeLeaveId != null) {
                stmt.setInt(paramIndex, excludeLeaveId);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error checking overlapping leave", ex);
            throw new RuntimeException("Failed to check overlapping leave", ex);
        }
        
        return false;
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