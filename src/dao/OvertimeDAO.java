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
import model.Overtime;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class OvertimeDAO {
    public List<Overtime> getOvertimeByEmployeeId(int empId) {
        List<Overtime> list = new ArrayList<>();
        String query = "SELECT * FROM overtime WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Overtime o = new Overtime();
                o.setOvertimeId(rs.getInt("overtime_id"));
                o.setEmployeeId(rs.getInt("employee_id"));
                o.setDate(rs.getDate("date"));
                o.setHours(rs.getDouble("hours"));
                list.add(o);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public List<Overtime> getOvertimeByEmployeeIdAndDateRange(int employeeId, LocalDate periodStart, LocalDate periodEnd) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}