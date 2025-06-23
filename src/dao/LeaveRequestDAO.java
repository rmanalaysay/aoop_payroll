/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author rejoice
 */
import util.DBConnection;
import model.LeaveRequest;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class LeaveRequestDAO {
    public List<LeaveRequest> getApprovedLeavesByEmployeeId(int empId) {
        List<LeaveRequest> list = new ArrayList<>();
        String query = "SELECT * FROM leave_request WHERE employee_id = ? AND status = 'Approved'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LeaveRequest lr = new LeaveRequest();
                lr.setLeaveId(rs.getInt("leave_id"));
                lr.setEmployeeId(rs.getInt("employee_id"));
                lr.setLeaveType(rs.getString("leave_type"));
                lr.setStartDate(rs.getDate("start_date"));
                lr.setEndDate(rs.getDate("end_date"));
                lr.setStatus(rs.getString("status"));
                list.add(lr);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public List<LeaveRequest> getApprovedLeavesByEmployeeIdAndDateRange(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}