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
import model.Attendance;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceDAO {
    public List<Attendance> getAttendanceByEmployeeId(int empId) {
        List<Attendance> list = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Attendance a = new Attendance();
                a.setAttendanceId(rs.getInt("attendance_id"));
                a.setEmployeeId(rs.getInt("employee_id"));
                a.setDate(rs.getDate("date"));
                a.setLoginTime(rs.getTime("login_time"));
                a.setLogoutTime(rs.getTime("logout_time"));
                list.add(a);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public void insertAttendance(Attendance attendance) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Attendance> getAttendanceByEmployeeIdBetweenDates(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
