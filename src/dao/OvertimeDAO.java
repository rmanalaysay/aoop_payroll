package dao;

import util.DBConnection;
import model.Overtime;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object for Overtime operations
 * @author rejoice
 */
public class OvertimeDAO {
    private static final Logger logger = Logger.getLogger(OvertimeDAO.class.getName());
    
    /**
     * Retrieves all overtime records for a specific employee
     * @param empId Employee ID
     * @return List of overtime records
     */
    public List<Overtime> getOvertimeByEmployeeId(int empId) {
        if (empId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        
        List<Overtime> list = new ArrayList<>();
        String query = "SELECT * FROM overtime WHERE employee_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Overtime o = mapResultSetToOvertime(rs);
                    list.add(o);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving overtime for employee ID: " + empId, ex);
            throw new RuntimeException("Failed to retrieve overtime records", ex);
        }

        return list;
    }

    /**
     * Retrieves overtime records for an employee within a date range
     * @param employeeId Employee ID
     * @param periodStart Start date of the period
     * @param periodEnd End date of the period
     * @return List of overtime records within the date range
     */
    public List<Overtime> getOvertimeByEmployeeIdAndDateRange(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period start and end dates cannot be null");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Period start date cannot be after end date");
        }
        
        List<Overtime> list = new ArrayList<>();
        String query = "SELECT * FROM overtime WHERE employee_id = ? AND date >= ? AND date <= ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Overtime o = mapResultSetToOvertime(rs);
                    list.add(o);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving overtime for employee ID: " + employeeId + 
                      " between dates: " + periodStart + " and " + periodEnd, ex);
            throw new RuntimeException("Failed to retrieve overtime records", ex);
        }

        return list;
    }
    
    /**
     * Inserts a new overtime record
     * @param overtime Overtime object to insert
     * @return Generated overtime ID
     */
    public int insertOvertime(Overtime overtime) {
        if (overtime == null) {
            throw new IllegalArgumentException("Overtime cannot be null");
        }
        if (overtime.getEmployeeId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (overtime.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (overtime.getHours() < 0) {
            throw new IllegalArgumentException("Hours cannot be negative");
        }
        
        String query = "INSERT INTO overtime (employee_id, date, hours) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, overtime.getEmployeeId());
            stmt.setDate(2, overtime.getDate());
            stmt.setDouble(3, overtime.getHours());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating overtime failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    overtime.setOvertimeId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating overtime failed, no ID obtained.");
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error inserting overtime record", ex);
            throw new RuntimeException("Failed to insert overtime record", ex);
        }
    }
    
    /**
     * Updates an existing overtime record
     * @param overtime Overtime object with updated information
     * @return true if update was successful
     */
    public boolean updateOvertime(Overtime overtime) {
        if (overtime == null) {
            throw new IllegalArgumentException("Overtime cannot be null");
        }
        if (overtime.getOvertimeId() <= 0) {
            throw new IllegalArgumentException("Overtime ID must be positive");
        }
        
        String query = "UPDATE overtime SET employee_id = ?, date = ?, hours = ? WHERE overtime_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, overtime.getEmployeeId());
            stmt.setDate(2, overtime.getDate());
            stmt.setDouble(3, overtime.getHours());
            stmt.setInt(4, overtime.getOvertimeId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating overtime record", ex);
            throw new RuntimeException("Failed to update overtime record", ex);
        }
    }
    
    /**
     * Calculates total overtime hours for an employee within a date range
     * @param employeeId Employee ID
     * @param periodStart Start date of the period
     * @param periodEnd End date of the period
     * @return Total overtime hours
     */
    public double getTotalOvertimeHours(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period start and end dates cannot be null");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Period start date cannot be after end date");
        }
        
        String query = "SELECT SUM(hours) as total_hours FROM overtime WHERE employee_id = ? AND date >= ? AND date <= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_hours");
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error calculating total overtime hours", ex);
            throw new RuntimeException("Failed to calculate total overtime hours", ex);
        }
        
        return 0.0;
    }
    
    /**
     * Maps ResultSet to Overtime object
     * @param rs ResultSet from database
     * @return Overtime object
     * @throws SQLException if database access error occurs
     */
    private Overtime mapResultSetToOvertime(ResultSet rs) throws SQLException {
        Overtime o = new Overtime();
        o.setOvertimeId(rs.getInt("overtime_id"));
        o.setEmployeeId(rs.getInt("employee_id"));
        o.setDate(rs.getDate("date"));
        o.setHours(rs.getDouble("hours"));
        return o;
    }
}