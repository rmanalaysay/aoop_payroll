package dao;

import util.DBConnection;
import model.Attendance;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Data Access Object for Attendance operations
 * @author rejoice
 */
public class AttendanceDAO {
    private static final Logger logger = Logger.getLogger(AttendanceDAO.class.getName());
    
    /**
     * Retrieves all attendance records for a specific employee
     * @param empId Employee ID
     * @return List of attendance records
     */
    public List<Attendance> getAttendanceByEmployeeId(int empId) {
        if (empId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE employee_id = ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = mapResultSetToAttendance(rs);
                    list.add(a);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving attendance for employee ID: " + empId, ex);
            throw new RuntimeException("Failed to retrieve attendance records", ex);
        }

        return list;
    }

    /**
     * Retrieves attendance records for an employee within a date range
     * @param employeeId Employee ID
     * @param periodStart Start date of the period
     * @param periodEnd End date of the period
     * @return List of attendance records within the date range
     */
    public List<Attendance> getAttendanceByEmployeeIdBetweenDates(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period start and end dates cannot be null");
        }
        if (periodStart.isAfter(periodEnd)) {
            throw new IllegalArgumentException("Period start date cannot be after end date");
        }
        
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE employee_id = ? AND date >= ? AND date <= ? ORDER BY date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance a = mapResultSetToAttendance(rs);
                    list.add(a);
                }
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving attendance for employee ID: " + employeeId + 
                      " between dates: " + periodStart + " and " + periodEnd, ex);
            throw new RuntimeException("Failed to retrieve attendance records", ex);
        }

        return list;
    }

    /**
     * Inserts a new attendance record
     * @param attendance Attendance object to insert
     * @return Generated attendance ID
     */
    public int insertAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        if (attendance.getEmployeeId() <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (attendance.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        String query = "INSERT INTO attendance (employee_id, date, login_time, logout_time) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, attendance.getEmployeeId());
            stmt.setDate(2, attendance.getDate());
            stmt.setTime(3, attendance.getLoginTime());
            stmt.setTime(4, attendance.getLogoutTime());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating attendance failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    attendance.setAttendanceId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating attendance failed, no ID obtained.");
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error inserting attendance record", ex);
            throw new RuntimeException("Failed to insert attendance record", ex);
        }
    }

    /**
     * Updates an existing attendance record
     * @param attendance Attendance object with updated information
     * @return true if update was successful
     */
    public boolean updateAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        if (attendance.getAttendanceId() <= 0) {
            throw new IllegalArgumentException("Attendance ID must be positive");
        }
        
        String query = "UPDATE attendance SET employee_id = ?, date = ?, login_time = ?, logout_time = ? WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, attendance.getEmployeeId());
            stmt.setDate(2, attendance.getDate());
            stmt.setTime(3, attendance.getLoginTime());
            stmt.setTime(4, attendance.getLogoutTime());
            stmt.setInt(5, attendance.getAttendanceId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating attendance record", ex);
            throw new RuntimeException("Failed to update attendance record", ex);
        }
    }

    /**
     * Deletes an attendance record
     * @param attendanceId Attendance ID to delete
     * @return true if deletion was successful
     */
    public boolean deleteAttendance(int attendanceId) {
        if (attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance ID must be positive");
        }
        
        String query = "DELETE FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, attendanceId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting attendance record", ex);
            throw new RuntimeException("Failed to delete attendance record", ex);
        }
    }

    /**
     * Retrieves attendance record by ID
     * @param attendanceId Attendance ID
     * @return Attendance object or null if not found
     */
    public Attendance getAttendanceById(int attendanceId) {
        if (attendanceId <= 0) {
            throw new IllegalArgumentException("Attendance ID must be positive");
        }
        
        String query = "SELECT * FROM attendance WHERE attendance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, attendanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAttendance(rs);
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error retrieving attendance by ID: " + attendanceId, ex);
            throw new RuntimeException("Failed to retrieve attendance record", ex);
        }
        
        return null;
    }

    /**
     * Checks if attendance exists for employee on specific date
     * @param employeeId Employee ID
     * @param date Date to check
     * @return true if attendance exists
     */
    public boolean attendanceExistsForDate(int employeeId, LocalDate date) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        
        String query = "SELECT COUNT(*) FROM attendance WHERE employee_id = ? AND date = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error checking attendance existence", ex);
            throw new RuntimeException("Failed to check attendance existence", ex);
        }
        
        return false;
    }

    /**
     * Counts total attendance days for employee within date range
     * @param employeeId Employee ID
     * @param periodStart Start date
     * @param periodEnd End date
     * @return Total attendance days
     */
    public int countAttendanceDays(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be positive");
        }
        if (periodStart == null || periodEnd == null) {
            throw new IllegalArgumentException("Period dates cannot be null");
        }
        
        String query = "SELECT COUNT(*) FROM attendance WHERE employee_id = ? AND date >= ? AND date <= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, employeeId);
            stmt.setDate(2, Date.valueOf(periodStart));
            stmt.setDate(3, Date.valueOf(periodEnd));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error counting attendance days", ex);
            throw new RuntimeException("Failed to count attendance days", ex);
        }
        
        return 0;
    }
    
    /**
     * Maps ResultSet to Attendance object
     * @param rs ResultSet from database
     * @return Attendance object
     * @throws SQLException if database access error occurs
     */
    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setEmployeeId(rs.getInt("employee_id"));
        a.setDate(rs.getDate("date"));
        a.setLoginTime(rs.getTime("login_time"));
        a.setLogoutTime(rs.getTime("logout_time"));
        return a;
    }
}
