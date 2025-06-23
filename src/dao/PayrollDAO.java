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
import model.Payroll;
import java.sql.*;
import java.util.*;

public class PayrollDAO {
    public List<Payroll> getPayrollByEmployeeId(int empId) {
        List<Payroll> list = new ArrayList<>();
        String query = "SELECT * FROM payroll WHERE employee_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, empId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Payroll p = new Payroll();
                p.setPayrollId(rs.getInt("payroll_id"));
                p.setEmployeeId(rs.getInt("employee_id"));
                p.setPeriodStart(rs.getDate("period_start"));
                p.setPeriodEnd(rs.getDate("period_end"));
                p.setGrossPay(rs.getDouble("gross_pay"));
                p.setNetPay(rs.getDouble("net_pay"));
                list.add(p);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }
}